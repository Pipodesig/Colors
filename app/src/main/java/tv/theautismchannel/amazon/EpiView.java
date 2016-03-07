package tv.theautismchannel.amazon;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Genius on 06.03.2016.
 */
public class EpiView extends LinearLayout {


    private TextView ep;
    private TextView title;
    private TextView description;
    private ImageView epiThumb;

    public EpiView(Context context) {
        super(context);

        View.inflate(context, R.layout.list_epi_text, this);
        ep = (TextView) findViewById(R.id.epi_num);
        title = (TextView) findViewById(R.id.epi_title);
        description = (TextView) findViewById(R.id.epi_des);
        epiThumb = (ImageView) findViewById(R.id.epi_thumb);

        ep.setText(R.string.ep);

    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void setDescription(String text) {
        description.setText(text);
    }
    public void setThumb(BitmapDrawable img) {
        epiThumb.setImageDrawable(img);
    }

}