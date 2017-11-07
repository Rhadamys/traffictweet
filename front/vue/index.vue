<template>
    <div class="container">
        <div class="col-md-10 col-md-offset-1">
            <div class="panel panel-default" style="height: 80vh">
                <div class="panel-body">
                    <div v-if="categoryMetrics.length > 0" class="reports-heading">
                        <b style="padding-right: 1em; margin-right: 1em; border-right: 1px solid #bbbbbb">Reportados hoy</b>
                        <div class="report-item" v-for="metric in categoryMetrics">
                            {{ metric.category.name }}: {{ metric.count }}
                        </div>
                    </div>
                    <b v-else="categoryMetrics.length > 0" class="text-center report-empty">
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
import GeoJSON from './rm.json';

export default {
    data: function () {
        return {
            categoryMetrics: [],
            communeMetrics: [],
            rm: GeoJSON,
            geojson: null,
            info: null,
            colors: [
                '#ffffe5',
                '#e5f5e0',
                '#c7e9c0',
                '#a1d99b',
                '#74c476',
                '#41ab5d',
                '#238b45',
                '#005a32'
            ]
        }
    },
    components: {
        'tweet': Tweet
    },
    mounted: function () {
        this.putMap();

        this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/metrics/categories/today')
            .then(response => {
                this.categoryMetrics = response.body;
            }, response => {
                console.log('Error cargando lista');
            });

        this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/metrics/communes/today')
            .then(response => {
                this.communeMetrics = response.body;
                this.putChoropleth();
            }, response => {
                console.log('Error cargando lista');
            });
    },
    methods: {
        putMap: function() {
            this.map = L.map('map').setView([-33.6093118, -70.7915517], 9);
            L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
                maxZoom: 18,
                id: 'mapbox.streets-basic',
                accessToken: 'pk.eyJ1IjoicmhhZGFteXMiLCJhIjoiY2o4bzBoZGVxMWU3bzJ3cGZtdTJucXkyMiJ9.pkzq5crdrE9U5HpXdl6Ing'
            }).addTo(this.map);
        },
        putChoropleth: function() {
            this.setMaxValue();
            this.geojson = L.geoJSON(this.rm, {
                style: this.style,
                onEachFeature: this.onEachFeature
            });
            this.geojson.addTo(this.map);

            this.addPopUps(this.$http);
        },
        addPopUps: function(http) {
            this.info = L.control();
            this.info.http = http;

            this.info.onAdd = function (map) {
                this._div = L.DomUtil.create('div', 'info'); // create a div with a class "info"
                this.update();
                return this._div;
            };

            // method that we will use to update the control based on feature properties passed
            this.info.update = function (props) {
                this._div.innerHTML = '<h4>Eventos por comuna</h4><br>';
                if(props) {
                    this.http.get('http://traffictweet.ddns.net:9090/traffictweet/metrics?commune=' + props.commune.name)
                        .then(response => {
                            this._div.innerHTML += props.commune.name + '</b><br>Total de eventos: ' + props.count + '<br><br><b>Detalle:</b><br><ul>';
                            response.body.forEach((metric) => {
                                this._div.innerHTML += '<li><b>' + metric.category.name + ':</b> ' + metric.count + '</li>';
                            });
                            this._div.innerHTML += '</ul>';
                        }, response => {
                            console.log('Error cargando lista');
                        });
                } else {
                    this._div.innerHTML += 'Posicione el mouse sobre una comuna...';
                }
            };

            this.info.addTo(this.map);
        },
        // Funciones para mapa coroplético
        setMaxValue: function() {
          let max = 0;
          this.communeMetrics.forEach((metric) => {
              if(metric.count > max) max = metric.count;
          });
          this.communeMetrics.max = max;
        },
        style: function(feature) {
            let metric = this.findMetric(feature);
            return {
                fillColor: this.getColor(metric ? metric.count : 0),
                weight: 2,
                opacity: 0.8,
                color: '#003933',
                dashArray: '3',
                fillOpacity: 0.75
            };
        },
        findMetric: function(feature) {
            let metricRet = null;
            this.communeMetrics.forEach((metric) => {
                if(metric.commune.name === feature.properties.Comuna) metricRet = metric;
            });
            return metricRet;
        },
        getColor: function(val) {
            return this.colors[parseInt(val * this.colors.length / this.communeMetrics.max)];
        },
        // Funciones para pop-ups
        highlightFeature: function(e) {
            var layer = e.target;

            layer.setStyle({
                weight: 5,
                color: '#666',
                dashArray: '',
                fillOpacity: 0.7
            });

            if (!L.Browser.ie && !L.Browser.opera && !L.Browser.edge) {
                layer.bringToFront();
            }

            this.info.update(this.findMetric(layer.feature));
        },
        resetHighlight: function(e) {
            this.geojson.resetStyle(e.target);
            this.info.update();
        },
        zoomToFeature: function(e) {
            this.map.fitBounds(e.target.getBounds());
        },
        onEachFeature: function(feature, layer) {
            layer.on({
                mouseover: this.highlightFeature,
                mouseout: this.resetHighlight,
                click: this.zoomToFeature
            });
        }
    }
}
</script>