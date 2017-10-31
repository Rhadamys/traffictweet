<template>
    <div class="container">
        <div class="col-md-10 col-md-offset-1">
            <div class="panel panel-default" style="height: 80vh">
                <div class="panel-body">
                    <div v-if="metrics.length > 0" class="reports-heading">
                        <b style="padding-right: 1em; margin-right: 1em; border-right: 1px solid #bbbbbb">Reportados hoy</b>
                        <div class="report-item" v-for="metric in metrics">
                            {{ metric.category.name }}: {{ metric.count }}
                        </div>
                    </div>
                    <b v-else="metrics.length > 0" class="text-center report-empty">
                        ¡No se han reportado nuevos eventos! <i class="fa fa-thumbs-o-up" aria-hidden="true"></i>
                    </b>
                </div>
                <div id="map" class="panel-body" style="height: 100%; width: 100%"></div>
            </div>
        </div>
    </div>
</template>
<script>
import Tweet from './tweet.vue';

export default {
    data: function () {
        return {
            metrics: []
        }
    },
    components: {
        'tweet': Tweet
    },
    mounted: function () {
        this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/metrics/today')
            .then(response=>{
                this.metrics = response.body;
            }, response=>{
                console.log('Error cargando lista');
            });

        this.map = L.map('map').setView([-33.6093118, -70.7915517], 9);

        L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
            maxZoom: 18,
            id: 'mapbox.streets-basic',
            accessToken: 'pk.eyJ1IjoicmhhZGFteXMiLCJhIjoiY2o4bzBoZGVxMWU3bzJ3cGZtdTJucXkyMiJ9.pkzq5crdrE9U5HpXdl6Ing'
        }).addTo(this.map);
    }
}
</script>