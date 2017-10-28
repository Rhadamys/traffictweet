<template>
    <div class="panel">
        <div class="panel-heading">
            <h4>
                @{{ username }}
                <img v-bind:src="image"
                     class="pull-left img-circle img-responsive"
                     style="padding: 0px; margin-right: 1em; height: 1.2em;"
                     alt="Imagen de usuario"/>
            </h4>
        </div>
        <div class="panel-body">
            <span v-for="category in categories" class="badge" v-bind:class="category.key">{{ category.name }}</span>
        </div>
        <div class="panel-body">
            {{ content }}
        </div>
        <div class="panel-footer">
            <a class="btn-block text-right" target="_blank">Ver detalles...</a>
        </div>
    </div>
</template>
<script>
export default {
    props: ["image", "username", "content", "categories"]
}
</script>