<template>
    <div class="container">
        <ol class="breadcrumb">
            <li><a href="#/">Inicio</a></li>
            <li><a href="#/panel-tweets">Panel de tweets</a></li>
            <li class="active">Detalle</li>
        </ol>
        <div class="col-md-4">
            <tweet v-bind="occurrence"></tweet>
        </div>
        <div class="col-md-8" style="height: 76vh">
            <div id="map" class="panel-body" style="height: 100%; width: 100%"></div>
        </div>
    </div>
</template>
<script>
import Tweet from './tweet.vue';

export default {
    props: ["id"],
    data: function () {
        return {
            occurrence: {}
        }
    },
    components: {
        'tweet': Tweet
    },
    mounted: function() {
        this.$http.get("http://localhost:9090/occurrences/" + this.id)
            .then(response=>{
                this.occurrence = response.body;

                if(this.occurrence.commune) {
                    this.map = L.map('map').setView(
                        [this.occurrence.commune.x, this.occurrence.commune.y],
                        this.occurrence.commune.z);
                } else {
                    this.map = L.map('map').setView([-33.6093118, -70.7915517], 9);
                }

                L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
                    maxZoom: 18,
                    id: 'mapbox.streets-basic',
                    accessToken: 'pk.eyJ1IjoicmhhZGFteXMiLCJhIjoiY2o4bzBoZGVxMWU3bzJ3cGZtdTJucXkyMiJ9.pkzq5crdrE9U5HpXdl6Ing'
                }).addTo(this.map);
            }, response=>{
                console.log('Error cargando evento');
            });
    }
}
</script>