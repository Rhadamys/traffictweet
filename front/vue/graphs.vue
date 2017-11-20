<template>
    <div class="container">
        <div class="row graphs">
            <div class="col-md-7 area">
                <div class="panel panel-default column">
                    <div class="panel-heading graph-header">
                        <div class="dates-header">
                            <b>Seleccione una fecha:</b>
                        </div>
                        <input class="form-control date-picker"
                               type="date" v-model="occurrencesDate"
                               placeholder="dd/MM/aaaa" id="categories-date">
                    </div>
                    <div id="map" class="area"></div>
                </div>
            </div>
            <div class="col-md-5 area">
                <div class="panel panel-default column graph">
                    <canvas id="chart-categories"></canvas>
                    <canvas id="chart-communes"></canvas>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import GeoJSON from './rm.json';

export default {
    data: function () {
        return {
            occurrencesDate: '',
            categoryChart: null,
            communeChart: null,
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
    mounted: function () {
        this.putMap();
        this.putGraphs();
    },
    methods: {
        putMap: function() {
            this.map = L.map('map').setView([-33.6093118, -70.7915517], 9);
            L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
                maxZoom: 18,
                id: 'mapbox.streets-basic',
                accessToken: 'pk.eyJ1IjoicmhhZGFteXMiLCJhIjoiY2o4bzBoZGVxMWU3bzJ3cGZtdTJucXkyMiJ9.pkzq5crdrE9U5HpXdl6Ing'
            }).addTo(this.map);

            this.addPopUps();
        },
        putChoropleth: function() {
            if(this.geojson) this.geojson.remove();
            if(this.communeMetrics.length > 0) {
                this.setMaxValue();
                this.geojson = L.geoJSON(this.rm, {
                    style: this.style,
                    onEachFeature: this.onEachFeature
                });
                this.geojson.addTo(this.map);
            } else {
                alert("No se registraron eventos en la fecha seleccionada. Por favor, seleccione otra fecha.");
            }
        },
        addPopUps: function() {
            this.info = L.control();
            this.info.http = this.$http;
            this.info.date = this.occurrencesDate;

            this.info.onAdd = function (map) {
                this._div = L.DomUtil.create('div', 'info'); // create a div with a class "info"
                this.update();
                return this._div;
            };

            // method that we will use to update the control based on feature properties passed
            this.info.update = function (props) {
                this._div.innerHTML = '<h4>Eventos por comuna</h4>';
                if(props) {
                    this.http.get('http://traffictweet.ddns.net:9090/traffictweet/metrics?commune=' + props.commune.name + "&date=" + this.date)
                        .then(response => {
                            this._div.innerHTML += '<h3>' + props.commune.name + '</h3></b><br>Total de eventos: ' + props.count + '<br><br><b>Detalle:</b><br><ul>';
                            response.body.forEach((metric) => {
                                this._div.innerHTML += '<li><b>' + metric.category.name + ':</b> ' + metric.count + '</li>';
                            });
                            this._div.innerHTML += '</ul>';
                        }, response => {
                            console.log('Error cargando lista');
                        });
                } else {
                    this._div.innerHTML += '<br>Posicione el mouse sobre una comuna...';
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
            const layer = e.target;
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
        },
        // Gráficos
        putGraphs: function() {
            const ctxCategories = document.getElementById("chart-categories").getContext('2d');
            this.categoryChart = new Chart(ctxCategories, {
                type: 'doughnut',
                data: {
                    labels: [],
                    datasets: [{
                        label: "# de eventos",
                        data: [],
                        backgroundColor: [],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    legend: {
                        display: false
                    },
                    title:{
                        display: true,
                        text: '# Eventos reportados (por categoría)'
                    }
                }
            });

            const ctxCommunes = document.getElementById("chart-communes").getContext('2d');
            this.communeChart = new Chart(ctxCommunes, {
                type: 'doughnut',
                data: {
                    labels: [],
                    datasets: [{
                        label: "# de eventos",
                        data: [],
                        backgroundColor: [],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    legend: {
                        display: false
                    },
                    title:{
                        display: true,
                        text: '# Eventos reportados (por comuna)'
                    }
                }
            });
        },
        updateCategoriesChart: function() {
            let categoriesLabels = [];
            let categoriesCount = [];
            let colors = [];
            this.categoryMetrics.forEach((metric) => {
                colors.push(this.colors[categoriesLabels.length  % (this.colors.length - 1) + 1]);
                categoriesLabels.push(metric.category.name);
                categoriesCount.push(metric.count);
            });

            this.categoryChart.data.datasets = [{
                label: "# de eventos",
                data: categoriesCount,
                backgroundColor: colors,
                borderWidth: 1
            }];
            this.categoryChart.data.labels = categoriesLabels;
            this.categoryChart.update();
        },
        updateCommunesChart: function() {
            let communesLabels = [];
            let communesCount = [];
            let colors = [];
            this.communeMetrics.forEach((metric) => {
                colors.push(this.colors[communesLabels.length  % (this.colors.length - 1) + 1]);
                communesLabels.push(metric.commune.name);
                communesCount.push(metric.count);
            });

            this.communeChart.data.datasets = [{
                label: "# de eventos",
                data: communesCount,
                backgroundColor: colors,
                borderWidth: 1
            }];
            this.communeChart.data.labels = communesLabels;
            this.communeChart.update();
        }
    },
    watch: {
        occurrencesDate: function(val) {
            this.info.date = val;
            this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/metrics/categories?date=' + val)
                .then(response => {
                    this.categoryMetrics = response.body;
                    this.updateCategoriesChart();
                }, response => {
                    console.log('Error cargando lista');
                });

            this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/metrics/communes?date=' + val)
                .then(response => {
                    this.communeMetrics = response.body;
                    this.putChoropleth();
                    this.updateCommunesChart();
                }, response => {
                    console.log('Error cargando lista');
                });
        }
    }
}
</script>