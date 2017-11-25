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
            this.$http.get('http://localhost:9090/occurrences')
                .then(response => {
                    this.calendar = response.body;
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
                        this.calendar = response.body;
                        this.loading = false;
                        this.searched = true;
                    });
            }
        }
    },
    watch: {
        search: function() {
            this.searched = false;
        }
    }
}
</script>