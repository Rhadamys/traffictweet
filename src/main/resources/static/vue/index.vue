<template>
    <div class="container">
        <div class="col-md-8">
            <div class="panel panel-default" style="height: 80vh">
                <div class="panel-body">
                    <b style="padding-right: 1em; margin-right: 1em; border-right: 1px solid #bbbbbb">Hoy</b>
                    Accidentes: 2&ensp;&ensp;
                    Puntos de congestión: 7&ensp;&ensp;
                    Otros: 17&ensp;&ensp;
                </div>
                <div id="map" class="panel-body" style="height: 71vh"/>
            </div>
        </div>
        <div class="col-md-4" style="border: 1px solid #bbbbbb; background: #efefef; overflow-y: scroll; height: 80vh">
            <h3 class="text-center">Últimos tweets</h3>
            <div v-for="tweet in tweets">
                <tweet v-bind="tweet"></tweet>
            </div>
        </div>
    </div>
</template>
<script>
import Tweet from './tweet.vue';

export default {
    data: function () {
        return {
            tweets: [
                {
                    user: 'Usuario 1',
                    text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce non vehicula ipsum. Sed odio orci, mollis vel tristique et, consectetur...',
                    category: 'Accidente',
                    image: '../img/user.png',
                    url: 'http://www.google.cl'
                }, {
                    user: 'Usuario 2',
                    text: 'Etiam id convallis orci, sed pretium magna. Nulla sed leo lorem. Donec in euismod est. Suspendisse ut sapien at dui tincidunt vulputate...',
                    category: 'Congestión',
                    image: '../img/user.png',
                    url: 'http://www.facebook.com'
                }, {
                    user: 'Usuario 3',
                    text: 'Duis gravida lectus ac faucibus vehicula. Sed nec neque interdum, fringilla diam venenatis, luctus urna. Vestibulum ante ipsum primis in...',
                    category: 'Congestión',
                    image: '../img/user.png',
                    url: 'http://www.youtube.cl'
                }
            ]
        }
    },
    components: {
        'tweet': Tweet
    },
    mounted: function () {
        this.map = L.map('map').setView([-33.4720684,-70.6807997], 11,5);

        L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
            maxZoom: 18,
            id: 'mapbox.streets-basic',
            accessToken: 'pk.eyJ1IjoicmhhZGFteXMiLCJhIjoiY2o4bzBoZGVxMWU3bzJ3cGZtdTJucXkyMiJ9.pkzq5crdrE9U5HpXdl6Ing'
        }).addTo(this.map);
    }
}
</script>