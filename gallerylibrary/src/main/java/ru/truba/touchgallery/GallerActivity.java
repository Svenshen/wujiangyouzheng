package ru.truba.touchgallery;


import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class GallerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        GalleryViewPager vp = (GalleryViewPager) findViewById(R.id.vp);
        vp.setAdapter(new BasePagerAdapter(this, getIntent()
                .getStringArrayListExtra("paths")));
        vp.setCurrentItem(getIntent().getIntExtra("index", 0));
        vp.setOnItemClickListener(new GalleryViewPager.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                finish();
            }
        });
    }
}
