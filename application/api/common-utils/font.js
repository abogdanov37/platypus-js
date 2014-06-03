(function() {
    var javaClass = Java.type("com.eas.gui.Font");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Font(null, null, null, aDelegate);
    });
    
    /**
    * Font object, which is used to render text in a visible way.
    * @param family a font family name, e.g. 'SansSerif'
    * @param style a FontStyle object
    * @param size the size of the font
     * @namespace Font
    */
    P.Font = function (family, style, size) {

        var maxArgs = 3;
        var delegate = arguments.length > maxArgs ?
            arguments[maxArgs] : new javaClass(P.boxAsJava(family), P.boxAsJava(style), P.boxAsJava(size));

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });

        delegate.setPublished(this);
    };
})();