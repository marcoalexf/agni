import React from 'react';
import './News.css';

var news = [
    { text: "Primeira Notícia", pic: "./img/news1.jpg"},
    { text: "Segunda Notícia", pic: "./img/news2.jpg"},
    { text: "Terceira Notícia", pic: "./img/news3.jpg"},
]

let NewsSection = () => <section id="news">
    <ul>
        {
            news.map( (n,i) =>
                <li>
                    <div className={"newspic"} id={"pic"+i}>
                        <img src={n.pic} alt={""}/>
                    </div>
                    <p>{n.text}</p>
                </li>
            )
        }
    </ul>
</section>;

export default NewsSection;