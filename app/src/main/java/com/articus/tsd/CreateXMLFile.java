package com.articus.tsd;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class CreateXMLFile {

    final String TAG = "CreateNewXML";
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder;
    Element element;
    Document doc;
    TransformerFactory transformerFactory;
    Transformer transformer;
    DOMSource domSource;
    StreamResult sr;
    String filepath = "storage/emulated/0/Download/";

    public void createDocument(String filename){
        try{
            File file = new File(filepath, filename);
            file = new File(filepath);
            file.mkdir();
            file = new File(filepath, filename);

            documentBuilderFactory.setNamespaceAware(false);
            documentBuilderFactory.setValidating(false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/validation", false);

            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            doc = documentBuilder.newDocument();
            element = doc.createElement("Table");
            doc.appendChild(element);
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            domSource = new DOMSource(doc);
            sr = new StreamResult(file);
            transformer.transform(domSource, sr);
        } catch (ParserConfigurationException | TransformerException pce){
            Log.e(TAG, "Error: ", pce);
        }
    }
    public void setAttr(String id, String code, String name, String count, String filename, File _file, Boolean save)
            throws TransformerException, IOException, SAXException, ParserConfigurationException {

        if (!_file.isFile())
        {
            createDocument(filename);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            doc = documentBuilder.parse(_file);
            element = doc.getDocumentElement();
            transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            domSource = new DOMSource(doc);
            sr = new StreamResult(_file);
        }
        try {
            Element goodsElement = doc.createElement("goods");
            element.appendChild(goodsElement);

            Element idElement = doc.createElement("id");
            idElement.setTextContent(id);
            goodsElement.appendChild(idElement);

            code = code.replaceAll("&", "&amp;");
            code = code.replaceAll("\"", "&quot;");
            code = code.replaceAll(">", "&gt;");
            code = code.replaceAll("<", "&lt;");
            code = code.replaceAll("'", "&apos;");

            Element codeElement = doc.createElement("code");
            codeElement.setTextContent(code.toString());
            goodsElement.appendChild(codeElement);

            name = name.replaceAll("&", "&amp;");
            name = name.replaceAll("\"", "&quot;");
            name = name.replaceAll(">", "&gt;");
            name = name.replaceAll("<", "&lt;");
            name = name.replaceAll("'", "&apos;");
            name = name.replaceAll(" ", "&#8;");
            Element nameElement = doc.createElement("name");
            nameElement.setTextContent(name.toString());
            goodsElement.appendChild(nameElement);

            Element countElement = doc.createElement("count");
            countElement.setTextContent(count);
            goodsElement.appendChild(countElement);

            if(save)
            transformer.transform(domSource, sr);

            Log.v(TAG, "Saved");
            Log.v(TAG, "Id:" + id);
            Log.v(TAG, "Code:" + code);
            Log.v(TAG, "Name:" + name);
            Log.v(TAG, "Count:" + count);
        } catch (Exception e){
            Log.e(TAG, "Error: ", e);
        }
    }
}
