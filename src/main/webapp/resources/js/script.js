function copyToClipboard(idTextArea) {
    var textArea = document.getElementById("form:" + idTextArea);
    textArea.select();
    document.execCommand('copy');
}

function toutEffacer() {
    document.getElementById("form:question").value = "";
    document.getElementById("form:reponse").value = "";
}
