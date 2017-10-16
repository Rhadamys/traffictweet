<template>
    <div>
        <nav class="navbar navbar-default navbar-static-top">
            <div class="container">
                <div class="navbar-header">
                    <a class="navbar-brand" href="#">TrafficTweet</a>
                </div>
                <div class="collapse navbar-collapse" id="app-navbar-collapse" style="padding-left: 10em">
                    <!-- Left Side Of Navbar -->
                    <a href="#/" class="btn navbar-btn" role="button">Inicio</a>
                    <a href="#/panel-tweets" class="btn navbar-btn" role="button">Panel de tweets</a>
                    <a href="#/graficos" class="btn navbar-btn" role="button">Gráficos</a>
                    <a href="#/nosotros" class="btn navbar-btn" role="button">Nosotros</a>
                </div>
            </div>
        </nav>
        <transition name="slide-fade" mode="out-in">
            <router-view></router-view>
        </transition>
    </div>
</template>
<script>
export default {}
</script>