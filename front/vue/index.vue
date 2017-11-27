<template>
    <div class="container">
        <div class="full-height full-height">
            <div class="panel panel-default full-height panel-flex">
                <div class="panel-body">
                    <div v-if="loading" class="text-center">
                        <i class="fa fa-circle-o-notch fa-spin" aria-hidden="true"></i> Cargando...
                    </div>
                    <div v-else-if="metrics.categoryMetrics.length > 0" class="reports-heading">
                        <b style="padding-right: 1em; margin-right: 1em; border-right: 1px solid #bbbbbb">Reportados hoy</b>
                        <div class="report-item" v-for="metric in metrics.categoryMetrics">
                            {{ metric.category }}: {{ metric.count }}
                        </div>
                    </div>
                    <b class="text-center report-empty" v-else>
                        ¡No se han reportado nuevos eventos! <i class="fa fa-thumbs-o-up" aria-hidden="true"></i>
                    </b>
                </div>
                <choropleth-map v-bind:metrics="metrics.communeMetrics"></choropleth-map>
            </div>
        </div>
    </div>
</template>
<script>
import Tweet from './tweet.vue';
import Map from './choropleth-map.vue';

export default {
    data: function () {
        return {
            metrics: {
                communeMetrics: [],
                categoryMetrics: []
            },
            loading: true
        }
    },
    components: {
        'choropleth-map': Map,
        'tweet': Tweet
    },
    mounted: function () {
        const today = new Date();
        const year = today.getFullYear();
        const month = today.getMonth() + 1;
        const day = today.getDate();
        const date = year + '-' + month + '-' + day;
        this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/metrics?from=' + date + '&to=' + date)
            .then(response => {
                this.metrics = response.body;
                this.loading = false;
            }, response => {
                console.log('Error cargando lista');
            });
    }
}
</script>