package com.example.marisco.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.NewsViewHolder> {

    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.cv) CardView cv;
        @BindView(R.id.news_card_title) TextView title;
        @BindView(R.id.news_card_description) TextView description;
        @BindView(R.id.news_card_image) ImageView background;

        NewsViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    List<Article> news_articles;

    RVAdapter(List<Article> articles){
        this.news_articles = articles;
    }

    @Override
    public int getItemCount(){
        return news_articles.size();
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup group, int i){
        View v = LayoutInflater.from(group.getContext()).inflate(R.layout.news_card, group, false);
        NewsViewHolder newsViewHolder = new NewsViewHolder(v);
        return newsViewHolder;
    }

    @Override
    public void onBindViewHolder(NewsViewHolder newsViewHolder, int i){
        final int position = i;
        newsViewHolder.title.setText(this.news_articles.get(i).getTitle());
        newsViewHolder.description.setText(this.news_articles.get(i).getDescription());
        Picasso.get().load(this.news_articles.get(i).getUrlToImage()).into(newsViewHolder.background);
        newsViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                launchWeb(v, position);
            }
        });
    }

    public void launchWeb(View v, int i){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.news_articles.get(i).getUrl()));
        v.getContext().startActivity(browserIntent);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView view){
        super.onAttachedToRecyclerView(view);
    }
}
