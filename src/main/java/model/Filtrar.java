/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author jhons
 */
public class Filtrar {

    private Document doc;

    public Filtrar(String caminho) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        DocumentBuilder construtor = fabrica.newDocumentBuilder();
        doc = construtor.parse(caminho);

    }

    private String serealizar(Node no) throws TransformerConfigurationException, TransformerException {
        TransformerFactory fabrica = TransformerFactory.newInstance();
        Transformer transformador = fabrica.newTransformer();
        DOMSource fonte = new DOMSource(no);
        ByteArrayOutputStream fluxo = new ByteArrayOutputStream();
        StreamResult saida = new StreamResult(fluxo);
        transformador.transform(fonte, saida);
        return fluxo.toString();
    }
    
    private Document newDocLivraria() throws ParserConfigurationException {
        Document newDoc;
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        DocumentBuilder construtor = fabrica.newDocumentBuilder();
        newDoc = construtor.newDocument();
        Element livraria=newDoc.createElement("livraria");
        newDoc.appendChild(livraria);
        return newDoc;
    }
    
    private void CopyForNewDoc(Node original, Document newDoc){
        NodeList filhos=original.getChildNodes();
        int tam = filhos.getLength();
        Element newBook=newDoc.createElement("livro");
        for(int i=0; i<tam ; i++) {
            Node filho=filhos.item(i);
            if(filho.getNodeType()==Node.ELEMENT_NODE){
                Element newTag = newDoc.createElement(filho.getNodeName());
                Text newText = newDoc.createTextNode(filho.getFirstChild().getNodeValue());
                newTag.appendChild(newText);
                newBook.appendChild(newTag);
            }
        }
        newDoc.getDocumentElement().appendChild(newBook);
    }
    
    private String FiltroGeral(String tag, String valor) throws TransformerException, ParserConfigurationException  {
        Document newDoc=newDocLivraria();
        Node noLivro = null;
        NodeList filhos = doc.getElementsByTagName(tag);
        int tam = filhos.getLength();
        for (int i = 0; i < tam; i++) {
            Node noFilho = filhos.item(i);
            if (noFilho != null) {
                if (noFilho.getFirstChild().getNodeValue().equals(valor)) {
                    noLivro = noFilho.getParentNode();
                    CopyForNewDoc(noLivro, newDoc);
                }
            }
        }
        return serealizar(newDoc);
    }

    public String FiltroTitulo(String titulo) throws TransformerException, ParserConfigurationException {
        return FiltroGeral("titulo", titulo);
    }
    
    public String FiltroAno(String ano) throws TransformerException, ParserConfigurationException {
        return FiltroGeral("ano", ano);
    }

    public boolean existeAutor(Element noLivro, String autor) {

        NodeList noAutores = noLivro.getElementsByTagName("autor");
        int tam = noAutores.getLength();

        for (int i = 0; i < tam; i++) {
            Node noAutor = noAutores.item(i);

            if (noAutor.getFirstChild().getNodeValue().equals(autor)) {
                return true;
            }
        }
        return false;
    }
    
    public String FiltroAutor(String autor) throws TransformerException, ParserConfigurationException{
        NodeList noLivros=doc.getElementsByTagName("livro");
        int tam=noLivros.getLength();
        Document newDoc=newDocLivraria();
        for(int i=0; i<tam ;i++)
        {
            Element noLivro=(Element)noLivros.item(i);
            if(existeAutor(noLivro,autor))
            {
                CopyForNewDoc(noLivro, newDoc);
            }
        }
        return serealizar(newDoc);
    }
    
    public String FiltroPreco(double preco) throws TransformerException, ParserConfigurationException{
        NodeList noPrecos=doc.getElementsByTagName("preco");
        Node noLivro = null;
        int tam=noPrecos.getLength();
        Document newDoc=newDocLivraria();
        for(int i=0; i<tam ;i++)
        {
            Element noPreco=(Element)noPrecos.item(i);
            String  valor = noPreco.getFirstChild().getNodeValue();
            double numeroConvertido = Double.parseDouble(valor);
            if(numeroConvertido<= preco){
                noLivro = noPreco.getParentNode();
                CopyForNewDoc(noLivro, newDoc);          
            }               
        }
        return serealizar(newDoc);
    }
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {
     //   Filtrar filtra = new Filtrar("src/main/java/model/livraria.xml");
     //   System.out.println(filtra.FiltroPreco(30));

    }

}
