<template>
    <div class="container charts">
        <div class="col-md-7 full-height">
            <div class="panel panel-flex full-height">
                <div class="panel-heading" style="padding-bottom: 0">
                    <div class="row">
                        <div class="col-md-9 dates-header">
                            <div class="form-group horizontal">
                                <label>Desde:</label>
                                <datepicker class="picker" v-bind="calendar" v-model="fromState.date" v-bind:disabled="fromState.disabled"
                                            v-on:selected="updateToDisabled"></datepicker>
                            </div>
                            <div class="form-group horizontal">
                                <label>Hasta:</label>
                                <datepicker class="picker" v-bind="calendar" v-model="toState.date" v-bind:disabled="toState.disabled"
                                            v-on:selected="updateFromDisabled"></datepicker>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <button class="btn btn-block btn-success" v-on:click="updateCharts">
                                <span class="glyphicon glyphicon-search" aria-hidden="true"></span>&emsp;Buscar
                            </button>
                        </div>
                    </div>
                </div>
                <choropleth-map v-bind:metrics="metrics.communeMetrics"></choropleth-map>
            </div>
        </div>
        <div class="col-md-5 full-height">
            <div class="panel panel-default full-height">
                <div class="column chart" v-show="!loading && metrics.communeMetrics.length > 0">
                    <canvas id="chart-categories"></canvas>
                    <canvas id="chart-communes"></canvas>
                </div>
                <div class="alert text-center full-height" v-if="loading">
                    <i class="fa fa-circle-o-notch fa-spin" aria-hidden="true" style="font-size: 10em;"></i>
                    <br>
                    <h3 class="text-center">Cargando...</h3>
                </div>
                <div class="alert text-center full-height" v-else-if="metrics.communeMetrics.length == 0">
                    <i class="fa fa-pie-chart" aria-hidden="true" style="font-size: 10em;"></i>
                    <h4 class="text-center"><b>No se han cargado datos</b></h4><br>
                    <p>
                        Seleccione un rango de fechas y presione
                        <b style="border: 1px solid #777777;
                                      padding: 0.5em;
                                      border-radius: 0.2em;">
                            <span class="glyphicon glyphicon-search" aria-hidden="true"></span> Buscar
                        </b>
                    </p>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import Datepicker from 'vuejs-datepicker';
import Map from './choropleth-map.vue';

export default {
    data: function () {
        return {
            categoryChart: null,
            communeChart: null,
            metrics: {
                communeMetrics: [],
                categoryMetrics: []
            },
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
            calendar: {
                language: 'es',
                inputClass: 'form-control date-picker',
                mondayFirst: true,
                format: 'dd/MM/yyyy',
            },
            fromState: {
                date: new Date(2017, 9, 27),
                disabled: {
                    to: new Date(2017, 9, 27),
                    from: new Date()
                }
            },
            toState: {
                date: new Date(),
                disabled: {
                    to: new Date(2017, 9, 27),
                    from: new Date()
                }
            },
            loading: false
        }
    },
    components: {
        'choropleth-map': Map,
        Datepicker
    },
    mounted: function () {
        this.putCharts();
    },
    methods: {
        // Gráficos
        putCharts: function() {
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
            this.metrics.categoryMetrics.forEach((metric) => {
                colors.push(this.colors[categoriesLabels.length  % (this.colors.length - 1) + 1]);
                categoriesLabels.push(metric.category);
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
            this.metrics.communeMetrics.forEach((metric) => {
                colors.push(this.colors[communesLabels.length  % (this.colors.length - 1) + 1]);
                communesLabels.push(metric.commune);
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
        },
        // Dates
        updateFromDisabled: function(date) {
            this.fromState.disabled.from = date;
        },
        updateToDisabled: function(date) {
            this.toState.disabled.to = date;
        },
        parseDate: function(date) {
            const year = date.getFullYear();
            const month = date.getMonth() + 1;
            const day = date.getDate();
            return year + '-' + month + '-' + day;
        },
        updateCharts: function() {
            this.loading = true;
            const from = this.parseDate(this.fromState.date);
            const to = this.parseDate(this.toState.date);

            this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/metrics?from=' + from + '&to=' + to)
                .then(response => {
                    this.metrics = response.body;
                    this.updateCategoriesChart();
                    this.updateCommunesChart();
                    this.loading = false;
                }, response => {
                    console.log('Error cargando lista');
                });
        }
    }
}
</script>