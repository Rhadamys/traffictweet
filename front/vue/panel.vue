<template>
    <div class="container">
        <ol class="breadcrumb">
            <li><a href="#/">Inicio</a></li>
            <li class="active">Panel de tweets</li>
        </ol>
        <div class="panel panel-body">
            <div class="row">
                <div class="col-md-3">
                    <select class="form-control" v-model="commune">
                        <option value="">Comuna...</option>
                        <option v-for="commune in communes">{{ commune.name }}</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <select class="form-control" v-model="category">
                        <option value="">Categoría...</option>
                        <option v-for="category in categories">{{ category.name }}</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <div class="input-group">
                        <input type="text" class="form-control" v-on:keyup.enter="searchTweets" v-model="search" placeholder="Buscar...">
                        <div class="input-group-btn">
                            <button class="btn btn-success" type="submit" v-on:click="searchTweets">
                                <i class="glyphicon glyphicon-search"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="alert text-center" v-if="loading">
            <i class="fa fa-circle-o-notch fa-spin" aria-hidden="true" style="font-size: 10em"></i>&ensp;
            <h3>Cargando...</h3>
        </div>
        <div v-else>
            <div v-if="calendar.length > 0">
                <div v-for="day in calendar">
                    <h3 v-if="calendar.length > 1" class="date-header">{{ day.date }}</h3>
                    <div class="row" v-for="i in Math.ceil(day.occurrences.length / 3)">
                        <div v-for="occurrence in day.occurrences.slice((i - 1) * 3, i * 3)" class="col-md-4">
                            <tweet v-bind="occurrence" v-bind:key="occurrence.tweetId" details="true"></tweet>
                        </div>
                    </div>
                </div>
            </div>
            <div class="alert text-center" v-else>
                <i class="fa fa-twitter" aria-hidden="true" style="font-size: 10em"></i>&ensp;
                <h3>No se han encontrado reportes... intenta aplicando un filtro de búsqueda</h3>
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
            commune: '',
            category: '',
            communes: [],
            categories: []
        }
    },
    components: {
        'tweet': Tweet
    },
    mounted: function () {
        this.loadData();
        this.tweetsOfToday();
    },
    methods: {
        loadData: function() {
            this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/communes')
                .then(response => {
                    this.communes = response.body;
                }, response => {
                    console.log('Error cargando lista');
                });

            this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/categories')
                .then(response => {
                    this.categories = response.body;
                }, response => {
                    console.log('Error cargando lista');
                });
        },
        tweetsOfToday: function () {
            this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/occurrences')
                .then(response => {
                    this.calendar = response.body;
                    this.loading = false;
                }, response => {
                    console.log('Error cargando lista');
                    this.loading = false;
                });
        },
        searchTweets: function () {
            if (this.search === '' && this.commune === '' && this.category === '') {
                this.tweetsOfToday();
            } else {
                this.loading = true;
                this.$http.get('http://traffictweet.ddns.net:9090/traffictweet/occurrences?search=' + this.search + '&commune=' +
                    this.commune + '&category=' + this.category)
                    .then(response => {
                        this.calendar = response.body;
                        this.loading = false;
                    });
            }
        }
    }
}
</script>