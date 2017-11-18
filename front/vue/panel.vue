<template>
    <div class="container">
        <ol class="breadcrumb">
            <li><a href="#/">Inicio</a></li>
            <li class="active">Panel de tweets</li>
        </ol>
        <div class="panel panel-body">
            <div class="input-group">
                <input type="text" class="form-control" v-on:keyup.enter="searchTweets" v-model="search" placeholder="Buscar...">
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
                            <tweet v-bind="tweet" v-bind:key="tweet.tweetId" details="true"></tweet>
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
            this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/occurrences?from=2017-11-07&to=2017-11-07')
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
                this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/occurrences?search=' + this.search)
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
                const match = /^(\d\d)\/(\d\d)\/(\d{4})/.exec(tweet.date);
                const day = Number(match[1]);
                const month = Number(match[2]);
                const year = Number(match[3]);
                let date = new Date(year, month, day);

                if (baseDate && date >= baseDate)
                    calendar[calendar.length - 1].tweets.push(tweet);
                else {
                    calendar.push({
                        date: day + ' de ' + monthNames[month - 1] + ' de ' + year,
                        tweets: [tweet]
                    });

                    baseDate = date;
                }
            });

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