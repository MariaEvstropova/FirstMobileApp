package com.evstropova.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    ImageView coverBig;
    TextView textGenre;
    TextView textStatistics;
    TextView textDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));

        coverBig = (ImageView) findViewById(R.id.cover_big);
        Picasso.with(this).load(getIntent().getStringExtra("cover")).into(coverBig);
        textGenre = (TextView) findViewById(R.id.text_genre);
        textGenre.setText(getIntent().getStringExtra("genres"));
        textStatistics = (TextView) findViewById(R.id.text_statistics);
        textStatistics.setText(getIntent().getStringExtra("statistics"));
        textDescription = (TextView) findViewById(R.id.text_description);
        textDescription.setText(getIntent().getStringExtra("description"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
