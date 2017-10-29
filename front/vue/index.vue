<template>
    <div class="container">
        <div class="col-md-10 col-md-offset-1">
            <div class="panel panel-default" style="height: 80vh">
                <div class="panel-body">
                    <b style="padding-right: 1em; margin-right: 1em; border-right: 1px solid #bbbbbb">Reportados hoy</b>
                    Accidentes: {{ accidentes }}&ensp;&ensp;
                    Congestión: {{ congestion }}&ensp;&ensp;
                    Desvíos: {{ desvios }}&ensp;&ensp;
                    Semáforos: {{ semaforos }}&ensp;&ensp;
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
            occurrences: [],
            accidentes: 0,
            congestion: 0,
            desvios: 0,
            semaforos: 0,
        }
    },
    components: {
        'tweet': Tweet
    },
    mounted: function () {
        this.$http.get('http://162.243.187.88:9090/traffictweet/occurrences/today')
            .then(response=>{
                this.occurrences = response.body;

                this.occurrences.forEach((occurrence) => {
                    occurrence.categories.forEach((category) => {
                        if(category.key === 'accidente')
                            this.accidentes++;
                        else if(category.key === 'congestion')
                            this.congestion++;
                        else if(category.key === 'desvio')
                            this.desvios++;
                        else
                            this.semaforos++;
                    })
                })
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