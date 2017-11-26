<template>
    <div id="map" style="z-index: 1" class="panel-body area"></div>
</template>
<script>
import GeoJSON from './rm.json';
export default {
    props: ['metrics'],
    data: function() {
        return {
            map: null,
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
            ],
            ignore: ['Comuna desconocida', 'Valparaíso', 'Viña del Mar']
        }
    },
    mounted: function() {
        this.map = L.map('map').setView([-33.6093118, -70.7915517], 9);
        L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
            maxZoom: 18,
            id: 'mapbox.streets-basic',
            accessToken: 'pk.eyJ1IjoicmhhZGFteXMiLCJhIjoiY2o4bzBoZGVxMWU3bzJ3cGZtdTJucXkyMiJ9.pkzq5crdrE9U5HpXdl6Ing'
        }).addTo(this.map);

        this.addPopUps();
    },
    methods: {
        putChoropleth: function() {
            if(this.geojson) this.geojson.remove();
            if(this.metrics.length > 0) {
                this.setMaxValue();
                this.geojson = L.geoJSON(this.rm, {
                    style: this.style,
                    onEachFeature: this.onEachFeature
                });
                this.geojson.addTo(this.map);
            }
        },
        addPopUps: function() {
            this.info = L.control();
            this.info.http = this.$http;
            this.info.comp = this;

            this.info.onAdd = function (map) {
                this._div = L.DomUtil.create('div', 'info'); // create a div with a class "info"
                this.update();
                return this._div;
            };

            // method that we will use to update the control based on feature properties passed
            this.info.update = function (props) {
                let body = '';
                if(props) {
                    body +=
                        '<h3 class="text-center"><b>' + props.commune + '</b></h3>'+
                        '<div class="table-responsive">' +
                        '<table class="table table-striped" style="margin-bottom: 0">' +
                        '<thead>' +
                        '<tr>' +
                        '<th class="text-center">Categoría</th>' +
                        '<th class="text-center">Total</th>' +
                        '</tr>' +
                        '</thead>' +
                        '<tbody>';
                    props.categories.forEach((metric) => {
                        body +=
                            '<tr>' +
                            '<td>' + metric.category + '</td>' +
                            '<td class="text-center">' + metric.count + '</td>' +
                            '</tr>';
                    });
                    body +=
                        '<tr class="success">' +
                        '<td class="text-right"><b>TOTAL</b></td>' +
                        '<td class="text-center"><b>' + props.count + '</b></td>' +
                        '</tr>' +
                        '</tbody>' +
                        '</table>' +
                        '</div>';
                    this._div.innerHTML = body;
                } else {
                    body +=
                        '<h4 class="text-center">Reportes por comuna</h4><br>' +
                        'Posicione el mouse sobre una comuna...' +
                        '<br><br><div style="height: 1em;" class="meter">';
                    this.comp.colors.forEach((color) => {
                        body += '<div style="background-color: ' + color + '; flex-grow: 1;"></div>';
                    });
                    body +=
                        '</div>' +
                        '<div style="justify-content: space-between; align-items: center" class="meter">' +
                            '<h4>' + 0 + '</h4>' +
                            (this.comp.metrics.max ?
                                '<h4>' + this.comp.metrics.max + '</h4>' :
                                '<i class="fa fa-circle-o-notch fa-spin" aria-hidden="true"></i>') +
                        '</div>';
                }

                this._div.innerHTML = body;
            };

            this.info.addTo(this.map);
        },
        // Funciones para mapa coroplético
        setMaxValue: function() {
            let max = 0;
            const comp = this;
            this.metrics.forEach((metric) => {
                const count = metric.count;
                const commune = metric.commune;
                if(count > max && !comp.ignore.includes(commune)) max = count;
            });
            this.metrics.max = max;
            this.info.update();
        },
        style: function(feature) {
            const metric = this.findMetric(feature);
            return {
                fillColor: metric ? this.getColor(metric.count): this.colors[0],
                weight: 2,
                opacity: 0.8,
                color: '#003933',
                dashArray: '3',
                fillOpacity: 0.75
            };
        },
        findMetric: function(feature) {
            let metricRet = null;
            const comp = this;
            this.metrics.forEach((metric) => {
                if(comp.clean(metric.commune) === comp.clean(feature.properties.Comuna)) metricRet = metric;
            });
            return metricRet;
        },
        getColor: function(val) {
            const index = parseInt(val * (this.colors.length - 1) / this.metrics.max);
            return this.colors[index === 0 ? 1: index];
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
        // Other
        clean: function(text) {
            const rep = "áéíóúüñÁÉÍÓÚÜÑ";
            const repWith = "aeiouunAEIOUUN";
            for(let i = 0; i < rep.length; i++)
                text = text.replace(rep[i], repWith[i]);
            return text.toLowerCase();
        }
    },
    watch: {
        metrics: function() {
            this.putChoropleth();
        }
    }
}
</script>