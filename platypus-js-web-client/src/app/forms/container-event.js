define(['./event', '../extend'], function(Event, extend){
    function ContainerEvent(aContainer, aChild) {
        Event.call(this, aContainer, aContainer);
        Object.defineProperty(this, 'child', {
            get: function(){
                return aChild;
            }
        });
    }
    extend(ContainerEvent, Event);
    return ContainerEvent;
});