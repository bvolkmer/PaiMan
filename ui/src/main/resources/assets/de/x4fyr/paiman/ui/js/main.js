
function showError(message) {
    ons.notification.toast(message, {timeout: 3000});
}

function clearChildren(node) {
    while (node.hasChildNodes()) {
        var firstChild = node.childNodes[0];
        clearChildren(firstChild);
        node.removeChild(firstChild)
    }
}