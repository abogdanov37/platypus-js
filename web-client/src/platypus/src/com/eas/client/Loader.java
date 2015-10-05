/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.queries.Query;
import com.eas.client.xhr.UrlQueryProcessor;
import com.eas.predefine.Utils;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xml.client.Document;

/**
 * 
 * @author mg
 */
public class Loader {

	public interface LoadHandler {

		public void started(String anItemName);

		public void loaded(String anItemName);
	}

	public static class LoadProcess {

		protected int expected;
		protected Callback<Void, String> callback;
		protected int calls;

		protected Set<String> loaded = new HashSet<>();
		protected Set<String> errors = new HashSet<>();

		public LoadProcess(int aExpected, Callback<Void, String> aCallback) {
			super();
			expected = aExpected;
			callback = aCallback;
		}

		public void complete(String aLoaded, String aFailured) {
			if (aLoaded != null) {
				loaded.add(aLoaded);
			}
			if (aFailured != null)
				errors.add(aFailured);
			if (++calls == expected) {
				if (errors.isEmpty())
					callback.onSuccess(null);
				else
					callback.onFailure(null);
			}
		}
	}

	public static final UrlQueryProcessor URL_QUERY_PROCESSOR = GWT.create(UrlQueryProcessor.class);
	public static final String INJECTED_SCRIPT_CLASS_NAME = "platypus-injected-script";
	public static final String SERVER_MODULE_TOUCHED_NAME = "Proxy-";
	public static final String MODEL_TAG_NAME = "datamodel";
	public static final String ENTITY_TAG_NAME = "entity";
	public static final String QUERY_ID_ATTR_NAME = "queryId";
	public static final String SCRIPT_SOURCE_TAG_NAME = "source";
	public static final String TYPE_JAVASCRIPT = "text/javascript";
	protected static final com.google.gwt.dom.client.Document htmlDom = com.google.gwt.dom.client.Document.get();
	protected static Set<LoadHandler> handlers = new HashSet<LoadHandler>();
	protected static Set<String> started = new HashSet<>();
	protected static Set<String> executed = new HashSet<>();
	protected static Map<String, JavaScriptObject> defined = new HashMap<>();
	protected static List<String> amdDependencies;
	protected static Callback<String, Void> amdDefineCallback;
	protected static Map<String, List<Callback<Void, String>>> pending = new HashMap<>();

	protected Loader() {
	}

	protected static void fireStarted(String anItemName) {
		for (LoadHandler lh : handlers) {
			lh.started(anItemName);
		}
	}

	protected static void fireLoaded(String anItemName) {
		for (LoadHandler lh : handlers) {
			lh.loaded(anItemName);
		}
	}

	public static HandlerRegistration addHandler(final LoadHandler aHandler) {
		handlers.add(aHandler);
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				handlers.remove(aHandler);
			}
		};
	}

	public static Map<String, JavaScriptObject> getDefined() {
		return defined;
	}

	public static void setAmdDefine(List<String> aDependencies, Callback<String, Void> aModuleDefiner) {
		amdDependencies = aDependencies;
		amdDefineCallback = aModuleDefiner;
	}

	public static List<String> consumeAmdDependencies() {
		List<String> res = amdDependencies;
		amdDependencies = null;
		return res;
	}

	public static Callback<String, Void> consumeAmdDefineCallback() {
		Callback<String, Void> res = amdDefineCallback;
		amdDefineCallback = null;
		return res;
	}

	public static void load(final Collection<String> aModulesNames, final Callback<Void, String> aCallback, final Set<String> aCyclic) throws Exception {
		if (!aModulesNames.isEmpty()) {
			final CumulativeCallbackAdapter<Void, String> process = new CumulativeCallbackAdapter<Void, String>(aModulesNames.size()) {

				@Override
				protected void failed(List<String> aReasons) {
					if (aCallback != null) {
						aCallback.onFailure(aReasons.toString());
					}
				}

				@Override
				protected void doWork(Void aResult) throws Exception {
					if (aCallback != null)
						aCallback.onSuccess(null);
				}

			};
			for (final String moduleName : aModulesNames) {
				if (executed.contains(moduleName) || aCyclic.contains(moduleName)) {
					Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

						@Override
						public void execute() {
							process.onSuccess(null);
						}
					});
				} else {
					aCyclic.add(moduleName);
					List<Callback<Void, String>> pendingOnModule = pending.get(moduleName);
					if (pendingOnModule == null) {
						pendingOnModule = new ArrayList<>();
						pending.put(moduleName, pendingOnModule);
					}
					pendingOnModule.add(process);
					if (!started.contains(moduleName)) {
						AppClient.getInstance().requestModuleStructure(moduleName, new CallbackAdapter<AppClient.ModuleStructure, XMLHttpRequest>() {

							@Override
							protected void doWork(AppClient.ModuleStructure aStructure) throws Exception {
								final CumulativeCallbackAdapter<String, String> moduleProcess = new CumulativeCallbackAdapter<String, String>(2) {

									@Override
									protected void failed(final List<String> aReasons) {
										List<Callback<Void, String>> interestedPendings = new ArrayList<>(pending.get(moduleName));
										pending.get(moduleName).clear();
										for (final Callback<Void, String> interestedPending : interestedPendings) {
											Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

												@Override
												public void execute() {
													interestedPending.onFailure(aReasons.toString());
												}
											});
										}
									}
									
									private void succeded(){
										List<Callback<Void, String>> interestedPendings = new ArrayList<>(pending.get(moduleName));
										pending.get(moduleName).clear();
										for (final Callback<Void, String> interestedPending : interestedPendings) {
											Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

												@Override
												public void execute() {
													interestedPending.onSuccess(null);
												}
											});
										}
									}

									@Override
									protected void doWork(String aJsPart) throws Exception {
										final String jsURL = AppClient.getInstance().checkedCacheBust(AppClient.relativeUri() + AppClient.APP_RESOURCE_PREFIX + aJsPart);
										ScriptInjector.fromUrl(jsURL).setCallback(new Callback<Void, Exception>() {

											@Override
											public void onSuccess(Void result) {
												executed.add(moduleName);
												fireLoaded(moduleName);
												final Callback<String, Void> amdDefineCallback = Loader.consumeAmdDefineCallback();
												List<String> amdDependencies = Loader.consumeAmdDependencies();
												if (amdDefineCallback != null) {
													try {
														Loader.load(amdDependencies, new Callback<Void, String>() {

															@Override
															public void onFailure(String reason) {
																failed(Arrays.asList(new String[]{reason}));
															}

															@Override
															public void onSuccess(Void result) {
																amdDefineCallback.onSuccess(moduleName);
																succeded();
															}

														}, aCyclic);
													} catch (Exception ex) {
														Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
													}
												} else {
													succeded();
												}
											}

											@Override
											public void onFailure(Exception reason) {
												failed(Arrays.asList(new String[] { reason.getMessage() }));
												Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, "Script [" + moduleName + "] is not loaded. Cause is: " + reason.getMessage());
											}

										}).setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(true).inject();
									}

								};
								final CumulativeCallbackAdapter<String, String> structureProcess = new CumulativeCallbackAdapter<String, String>(aStructure.getStructure().size()) {

									@Override
									protected void failed(List<String> aReasons) {
										moduleProcess.onFailure(aReasons.toString());
									}

									@Override
									protected void doWork(String aResult) throws Exception {
										moduleProcess.onSuccess(aResult);
									}

								};
								assert !aStructure.getStructure().isEmpty() : "Module [" + moduleName + "] structure should contain at least one element.";
								for (final String part : aStructure.getStructure()) {
									if (part.toLowerCase().endsWith(".js")) {
										Scheduler.get().scheduleDeferred(new ScheduledCommand() {
											@Override
											public void execute() {
												structureProcess.onSuccess(part);
											}
										});
									} else {
										AppClient.getInstance().requestDocument(part, new CallbackAdapter<Document, XMLHttpRequest>() {

											@Override
											public void onFailure(XMLHttpRequest reason) {
												structureProcess.onFailure(reason.getStatus() + " : " + reason.getStatusText());
											}

											@Override
											protected void doWork(Document aResult) throws Exception {
												structureProcess.onSuccess(null);
											}

										});
									}
								}
								final CumulativeCallbackAdapter<Void, String> dependenciesProcess = new CumulativeCallbackAdapter<Void, String>(3) {

									@Override
									protected void failed(List<String> aReasons) {
										moduleProcess.onFailure(aReasons.toString());
									}

									@Override
									protected void doWork(Void aResult) throws Exception {
										moduleProcess.onSuccess(null);
									}

								};
								load(aStructure.getClientDependencies(), dependenciesProcess, aCyclic);
								loadQueries(aStructure.getQueriesDependencies(), dependenciesProcess);
								loadServerModules(aStructure.getServerDependencies(), dependenciesProcess);
							}

							@Override
							public void onFailure(XMLHttpRequest reason) {
								process.onFailure(reason.getStatus() + ": " + reason.getStatusText());
							}
						});
						started.add(moduleName);
						fireStarted(moduleName);
					}
				}
			}
		} else {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

				@Override
				public void execute() {
					aCallback.onSuccess(null);
				}
			});
		}
	}

	public static void loadServerModules(Collection<String> aServerModulesNames, final Callback<Void, String> aCallback) throws Exception {
		if (!aServerModulesNames.isEmpty()) {
			final CumulativeCallbackAdapter<Void, String> process = new CumulativeCallbackAdapter<Void, String>(aServerModulesNames.size()) {

				@Override
				protected void failed(List<String> aReasons) {
					aCallback.onFailure(aReasons.toString());
				}

				@Override
				protected void doWork(Void aResult) throws Exception {
					aCallback.onSuccess(aResult);
				}
			};
			final Collection<Cancellable> startLoadings = new ArrayList<Cancellable>();
			for (final String appElementName : aServerModulesNames) {
				startLoadings.add(AppClient.getInstance().createServerModule(appElementName, new CallbackAdapter<Void, String>() {

					@Override
					public void doWork(Void aDoc) throws Exception {
						fireLoaded(SERVER_MODULE_TOUCHED_NAME + appElementName);
						process.onSuccess(aDoc);
					}

					@Override
					public void onFailure(String reason) {
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, reason);
						process.onFailure(reason);
					}
				}));
				fireStarted(SERVER_MODULE_TOUCHED_NAME + appElementName);
			}
		} else {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

				@Override
				public void execute() {
					aCallback.onSuccess(null);
				}
			});
		}
	}

	public static void loadQueries(Collection<String> aQueriesNames, final Callback<Void, String> aCallback) throws Exception {
		if (!aQueriesNames.isEmpty()) {
			final CumulativeCallbackAdapter<Void, String> process = new CumulativeCallbackAdapter<Void, String>(aQueriesNames.size()) {

				@Override
				protected void failed(List<String> aReasons) {
					aCallback.onFailure(aReasons.toString());
				}

				@Override
				protected void doWork(Void aResult) throws Exception {
					aCallback.onSuccess(aResult);
				}

			};
			final Collection<Cancellable> startLoadings = new ArrayList<Cancellable>();
			for (final String queryName : aQueriesNames) {
				startLoadings.add(AppClient.getInstance().getAppQuery(queryName, new CallbackAdapter<Query, String>() {

					@Override
					public void doWork(Query aQuery) throws Exception {
						fireLoaded(queryName);
						process.onSuccess(null);
					}

					@Override
					public void onFailure(String reason) {
						Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, reason);
						process.onFailure(reason);
					}
				}));
				fireStarted(queryName);
			}
		} else {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

				@Override
				public void execute() {
					aCallback.onSuccess(null);
				}
			});
		}
	}
	
	public static void jsLoadQueries(JavaScriptObject aQueries, final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) throws Exception {
		List<String> queries = new ArrayList<>();
		Utils.JsObject jsQueries = aQueries.cast();
		for (int i = 0; i < jsQueries.length(); i++) {
			queries.add(jsQueries.getString(i));
		}
		Loader.loadQueries(queries, new Callback<Void, String>() {

			@Override
			public void onSuccess(Void result) {
				if (aOnSuccess != null) {
					aOnSuccess.<Utils.JsObject> cast().apply(null, null);
				}
			}

			@Override
			public void onFailure(String aReason) {
				if (aOnFailure != null) {
					aOnFailure.<Utils.JsObject> cast().call(null, aReason);
				}
			}
		});
	}

	public static void jsLoadServerModules(JavaScriptObject aModulesNames, final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) throws Exception {
		List<String> modulesNames = new ArrayList<>();
		Utils.JsObject jsModulesNames = aModulesNames.cast();
		for (int i = 0; i < jsModulesNames.length(); i++) {
			modulesNames.add(jsModulesNames.getString(i));
		}
		Loader.loadServerModules(modulesNames, new Callback<Void, String>() {

			@Override
			public void onSuccess(Void result) {
				if (aOnSuccess != null) {
					aOnSuccess.<Utils.JsObject> cast().apply(null, null);
				}
			}

			@Override
			public void onFailure(String aReason) {
				if (aOnFailure != null) {
					aOnFailure.<Utils.JsObject> cast().call(null, aReason);
				}
			}
		});
	}
}