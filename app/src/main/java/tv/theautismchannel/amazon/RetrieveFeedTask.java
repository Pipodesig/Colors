package tv.theautismchannel.amazon;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;

import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;



class RetrieveFeedTask extends AsyncTask<String, Void, String> {

    private Exception exception;
    Document doc = null;
    public static int PRETTY_PRINT_INDENT_FACTOR = 4;
    String jsonPrettyPrintString;

    public interface AsyncResponse {
        void processFinish(String output) throws JSONException;
    }
    public AsyncResponse delegate = null;

    public RetrieveFeedTask(AsyncResponse delegate){
        this.delegate = delegate;
    }

    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(CollectionDemoActivity.STR);

            URLConnection conn = url.openConnection();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(conn.getInputStream());
            try {
                JSONObject xmlJSONObj = XML.toJSONObject(getStringFromDocument(doc));
                jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
            } catch (JSONException je) {
                je.printStackTrace();
            }
            return jsonPrettyPrintString;
        } catch (Exception e) {
            this.exception = e;
            e.printStackTrace();

            return null;
        }

    }

    protected void onPostExecute(String bldr) {
        try {
            delegate.processFinish(bldr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getStringFromDocument(Document doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

}