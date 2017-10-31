<template>
    <div class="panel">
        <div class="panel-heading">
            <div class="tweet-heading">
                <img v-bind:src="image"
                     class="img-circle img-responsive img"
                     alt="Imagen de usuario"/>
                @{{ username }}
            </div>
            <i class="tweet-date">
                {{ commune ? commune.name : 'Desconocida' }}&ensp;
                <i class="fa fa-map-marker" aria-hidden="true"></i>
            </i>
        </div>
        <div class="panel-body">
            <span v-for="category in categories" class="badge" v-bind:class="category.key">{{ category.name }}</span>
        </div>
        <div class="panel-body">
            {{ text }}
        </div>
        <div v-if="details" class="panel-footer tweet-footer">
            <i class="tweet-date">{{ dateString }}</i>
            <a v-bind:href="'#/occurrence/' + tweetId">Ver detalles...</a>
        </div>
    </div>
</template>
<script>
export default {
    props: ["tweetId", "image", "username", "text", "commune", "categories", "details", "dateString"],
}
</script>