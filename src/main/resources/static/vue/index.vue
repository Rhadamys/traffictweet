<template>
    <div class="container">
        <div class="col-md-10 col-md-offset-1">
            <div class="panel panel-default" style="height: 80vh">
                <div class="panel-body">
                    <b style="padding-right: 1em; margin-right: 1em; border-right: 1px solid #bbbbbb">Hoy</b>
                    Accidentes: 2&ensp;&ensp;
                    Puntos de congestión: 7&ensp;&ensp;
                    Otros: 17&ensp;&ensp;
                </div>
                <div id="map" class="panel-body" style="height: 100%; width: 100%"></div>
            </div>
        </div>
    </div>
</template>
<script>
import Tweet from './tweet.vue';

export default {
    components: {
        'tweet': Tweet
    },
    mounted: function () {
        this.map = L.map('map').setView([-33.6093118, -70.7915517], 9);

        L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
            maxZoom: 18,
            id: 'mapbox.streets-basic',
            accessToken: 'pk.eyJ1IjoicmhhZGFteXMiLCJhIjoiY2o4bzBoZGVxMWU3bzJ3cGZtdTJucXkyMiJ9.pkzq5crdrE9U5HpXdl6Ing'
        }).addTo(this.map);
    }
}
</script>