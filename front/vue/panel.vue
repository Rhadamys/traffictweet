<template>
    <div class="container">
        <ol class="breadcrumb">
            <li><a href="#/">Inicio</a></li>
            <li class="active">Panel de tweets</li>
        </ol>
        <div class="panel panel-body">
            <div class="input-group">
                <input type="text" class="form-control" v-model="search" placeholder="Buscar...">
                <div class="input-group-btn">
                    <button class="btn btn-success" type="submit" v-on:click="searchTweets">
                        <i class="glyphicon glyphicon-search"></i>
                    </button>
                </div>
            </div>
        </div>
        <div v-if="loading">
            <div class="loading text-center">
                <i class="fa fa-spinner" aria-hidden="true" style="font-size: 10em"></i>&ensp;
                <h3>Cargando...</h3>
            </div>
        </div>
        <div v-else>
            <div v-if="calendar.length > 0">
                <div v-for="date in calendar">
                    <h3 v-if="calendar.length > 1" class="date-header">{{ date.date }}</h3>
                    <div class="row" v-for="i in Math.ceil(date.tweets.length / 3)">
                        <div v-for="tweet in date.tweets.slice((i - 1) * 3, i * 3)" class="col-md-4">
                            <tweet v-bind="tweet" details="true"></tweet>
                        </div>
                    </div>
                </div>
            </div>
            <div class="loading text-center" v-else>
                <i class="fa fa-twitter" aria-hidden="true" style="font-size: 10em"></i>&ensp;
                <h3 v-if="search === '' && searched">Ningún reporte para hoy... intenta aplicando un filtro de búsqueda</h3>
                <h3 v-else>La búsqueda no ha arrojado resultados</h3>
            </div>
        </div>
    </div>
</template>
<script>
import Tweet from './tweet.vue';

export default {
    data: function () {
        return {
            calendar: [],
            loading: true,
            search: '',
            searched: true
        }
    },
    components: {
        'tweet': Tweet
    },
    mounted: function () {
        this.tweetsOfToday();
    },
    methods: {
        tweetsOfToday: function () {
            this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/occurrences')
                .then(response => {
                    this.calendar = this.getOccurrencesCalendar(response.body);
                    this.loading = false;
                }, response => {
                    console.log('Error cargando lista');
                    this.loading = false;
                });
        },
        searchTweets: function () {
            if (this.search === '') {
                this.tweetsOfToday();
            } else {
                this.loading = true;
                this.$http.get('http://localhost:9090/occurrences?search=' + this.search)
                    .then(response => {
                        this.calendar = this.getOccurrencesCalendar(response.body);
                        this.loading = false;
                        this.searched = true;
                    });
            }
        },
        getOccurrencesCalendar: function (tweets) {
            let monthNames = [
                'enero',
                'febrero',
                'marzo',
                'abril',
                'mayo',
                'junio',
                'julio',
                'agosto',
                'septiembre',
                'octubre',
                'noviembre',
                'diciembre'
            ];

            let calendar = [];
            let baseDate = null;
            tweets.forEach((tweet) => {
                let date = new Date(tweet.date);
                if (baseDate && date > baseDate)
                    calendar[calendar.length - 1].tweets.push(tweet);
                else {
                    date.setHours(0);
                    date.setMinutes(0);
                    date.setSeconds(0);

                    calendar.push({
                        date: date.getUTCDate() + ' de ' + monthNames[date.getMonth()] + ' de ' + date.getFullYear(),
                        tweets: [tweet]
                    });

                    baseDate = date;
                }
            });

            console.log(calendar)
            return calendar;
        }
    },
    watch: {
        search: function() {
            this.searched = false;
        }
    }
}
</script>