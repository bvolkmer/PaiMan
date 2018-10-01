package de.x4fyr.paiman.app.ui

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.ls.DOMImplementationLS
import javax.xml.parsers.DocumentBuilderFactory

fun NodeList.forEach(consumer: (Node) -> Unit) {
    for (i in 0 until this.length) {
        consumer(this.item(i))
    }
}

internal fun Element.forEachTagWithId(tagName: String, id: String, block: (Element) -> Unit) {
    getElementsByTagName(tagName).forEach {
        if (it is Element && it.getAttribute("id") == id) block(it)
    }
}

internal fun buildHTMLDocument() = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .parse(("<!DOCTYPE " + "html><html></html>").toByteArray().inputStream())!!

fun Node.produceString(): String = (this.ownerDocument.implementation.getFeature("LS",
        "3.0") as DOMImplementationLS).createLSSerializer().apply {
    domConfig.setParameter("xml-declaration", false)
}.writeToString(this)
        .replace("type=\"text/javascript\"/>", "type=\"text/javascript\"></script>")

internal fun Node.removeAllChildren() {
    while (hasChildNodes()) removeChild(lastChild)
}
