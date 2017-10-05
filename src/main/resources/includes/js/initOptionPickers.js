(function() {
    function createPicker($selectField) {
        new AJS.MultiSelect({
           element: $selectField,
           itemAttrDisplayed: "label",
           errorMessage: "Invalid Option selected!"
        });
    }

    function locateSelect(parent) {
        var $parent = AJS.$(parent),
            $selectField;

        if ($parent.is("select")) {
            $selectField = $parent;
        } else {
            $selectField = $parent.find("select");
        }

        return $selectField;
    }

    var DEFAULT_SELECTORS = [
        "div.aui-field-listoptionspicker", // aui forms
        "td.aui-field-listoptionspicker", // convert to subtask and move
        "tr.aui-field-listoptionspicker" // bulk edit
    ];

    function findOptionSelectAndConvertToPicker(context, selector) {
        selector = selector || DEFAULT_SELECTORS.join(", ");

        AJS.$(selector, context).each(function () {

            var $selectField = locateSelect(this);

            if ($selectField.length) {
                createPicker($selectField);
            }

        });
    }
    
    JIRA.bind(JIRA.Events.NEW_CONTENT_ADDED, function (e, context, reason) {
        if (reason !== JIRA.CONTENT_ADDED_REASON.panelRefreshed) {
            findOptionSelectAndConvertToPicker(context);
        }
    });

    AJS.$(function() {
        findOptionSelectAndConvertToPicker();
    });
})();