<template>
    <div class="container">
        <div class="col-md-5 panel-flex full-height">
            <tweet v-bind="occurrence"></tweet>
            <div class="panel panel-flex causes full-height">
                <div class="panel-heading text-center">
                    <h4><b>Eventos relacionados</b></h4>
                </div>
                <div v-if="loading" class="body panel-body text-center alert">
                    <i class="fa fa-circle-o-notch fa-spin" aria-hidden="true" style="font-size: 10em"></i>
                    <br>
                    <h3>Cargando...</h3>
                </div>
                <div v-else-if="causes.length > 0" class="table-responsive" style="overflow-y: auto;">
                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th>
                                <i class="fa fa-calendar" aria-hidden="true"></i> Fecha y hora
                            </th>
                            <th>
                                <i class="fa fa-map-marker" aria-hidden="true"></i> Comuna
                            </th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="cause in causes">
                            <td>{{ cause.date }}</td>
                            <td>{{ cause.commune.name }}</td>
                            <td>
                                <a class="btn btn-xs btn-default" v-bind:href="'#/occurrences/' + cause.tweetId">
                                    <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> Ver evento
                                </a>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="body panel-body text-center alert" v-else>
                    <i class="fa fa-car" aria-hidden="true" style="font-size: 7.5em"></i>
                    No se han detectado eventos causantes
                </div>
            </div>
        </div>
        <div class="col-md-7 full-height">
            <div class="panel full-height" id="graph">
                <div v-if="loading" class="panel alert full-height">
                    <i class="fa fa-circle-o-notch fa-spin" aria-hidden="true" style="font-size: 10em"></i>
                    <br>
                    <h3>Cargando...</h3>
                </div>
                <div v-else-if="graph" class="info node-info" id="info">
                    <b class="text-center">SIMBOLOGÍA</b>
                    <br><br>
                    <ul>
                        <li style="color: #DD1C1A">Evento</li>
                        <li style="color: #29BF12">Comuna</li>
                        <li style="color: #FF9914">Usuario</li>
                    </ul>
                    Posicione el mouse sobre un nodo para ver mayor información...
                </div>
                <div class="panel alert full-height" v-else>
                    <i class="fa fa-share-alt" aria-hidden="true" style="font-size: 10em"></i>&ensp;
                    <h3><b>No se pudo cargar el grafo</b></h3>
                    <h4>El evento no está registrado en la red...</h4>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
    import Tweet from './tweet.vue';

    export default {
        props: ["id"],
        data: function () {
            return {
                occurrence: {},
                causes: [],
                graph: false,
                loading: true
            }
        },
        components: {
            'tweet': Tweet
        },
        mounted: function () {
            this.loadData();
        },
        methods: {
            loadData: function () {
                this.$http.get("http://traffictweet.ddns.net:9090/traffictweet/occurrences/" + this.id)
                    .then(response => {
                        this.occurrence = response.body;
                        if (this.occurrence.commune !== 'Comuna desconocida')
                            this.putGraph();
                        else
                            this.graph = false;

                        this.loading = false;
                    }, response => {
                        console.log('Error cargando evento');
                    });
            },
            putGraph: function () {
                this.graph = true;
                d3.select("svg").remove();

                const graphElm = document.getElementById('graph');
                let width = graphElm.clientWidth, height = graphElm.clientHeight;
                // force layout setup
                let force = d3.layout.force()
                    .charge(-1000).linkDistance(250).size([width, height]);

                // setup svg div
                let svg = d3.select("#graph").append("svg:svg")
                    .attr("width", "100%").attr("height", "100%")
                    .attr("pointer-events", "all");

                // load graph (nodes,links) json from endpoint
                let causes = [];
                d3.json("http://traffictweet.ddns.net:9090/traffictweet/nodes/occurrences/" + this.id, function (error, graph) {
                    if (error) return;

                    graph.nodes[0].label = "current occurrence";
                    for (let i = 1; i < graph.nodes.length; i++) {
                        if (graph.nodes[i].name)
                            graph.nodes[i].label = "commune";
                        else if (graph.nodes[i].username)
                            graph.nodes[i].label = "user";
                        else {
                            graph.nodes[i].label = "occurrence";
                            causes.push(graph.nodes[i]);
                        }
                    }

                    force.nodes(graph.nodes)
                        .links(graph.links)
                        .start();

                    // render relationships as lines
                    let link = svg.selectAll(".link")
                        .data(graph.links).enter()
                        .append("line")
                        .attr("class", "link")
                        .attr('marker-end','url(#arrowhead)')
                        .style("stroke", "black")
                        .style("pointer-events", "none");

                    // render nodes as circles, css-class from label
                    let node = svg.selectAll(".node")
                        .data(graph.nodes).enter()
                        .append("circle")
                        .attr("r", 40)
                        .attr("class", function (d) {
                            return "node " + d.label;
                        })
                        .on("mouseover", function (d) {
                            switch (d.label) {
                                case 'current occurrence':
                                case 'occurrence': {
                                    info.innerHTML = '<b>Fecha:</b><br>' + d.date + '<br><br>' +
                                        '<b>Reportado en:</b><br>' + d.commune.name + '<br><br>' +
                                        '<b>Reportado por:</b><br>' + d.reportedBy.username;
                                    break;
                                }
                                case 'commune': {
                                    info.innerHTML = '<b>Comuna:</b><br>' + d.name;
                                    break;
                                }
                                case 'user': {
                                    info.innerHTML = '<b>Nombre de usuario:</b><br>' + d.username;
                                    break;
                                }
                            }
                        })
                        .on('mouseout', function (d) {
                            info.innerHTML = '<b style="text-align: center">SIMBOLOGÍA</b><br><br><ul>' +
                                '<li style="color: #DD1C1A">Evento</li>' +
                                '<li style="color: #29BF12">Comuna</li>' +
                                '<li style="color: #FF9914">Usuario</li>' +
                                '</ul>Posicione el mouse sobre un nodo para ver mayor información...';
                        })
                        .call(force.drag);

                    let nodelabels = svg.selectAll(".nodelabel")
                        .data(graph.nodes).enter()
                        .append('text')
                        .attr('text-anchor', 'middle')
                        .attr('dominant-baseline', 'central')
                        .attr('font-family', 'FontAwesome')
                        .attr('font-size', '1.5em')
                        .attr('fill', 'white')
                        .text(function (d) {
                            return d.label === 'commune' ? '\uf041':
                                d.label === 'user' ? '\uf007': '\uf071';
                        });

                    let edgepaths = svg.selectAll(".edgepath")
                        .data(graph.links).enter()
                        .append('path')
                        .attr({
                            'd': function (d) {
                                return 'M ' + d.source.x + ' ' + d.source.y + ' L ' +
                                    d.target.x + ' ' + d.target.y
                            },
                            'class': 'edgepath',
                            'fill-opacity': 0,
                            'stroke-opacity': 0,
                            'fill': 'blue',
                            'stroke': 'red',
                            'id': function (d, i) {
                                return 'edgepath' + i
                            }
                        })
                        .style("pointer-events", "none");

                    let edgelabels = svg.selectAll(".edgelabel")
                        .data(graph.links).enter()
                        .append('text')
                        .style("pointer-events", "none")
                        .attr({
                            'class': 'edgelabel',
                            'id': function (d, i) {
                                return 'edgelabel' + i
                            },
                            'dx': 60,
                            'dy': -5,
                            'font-size': 12,
                            'font-weight': 'bold',
                            'fill': '#005A32'
                        });

                    edgelabels.append('textPath')
                        .attr('xlink:href', function (d, i) {
                            return '#edgepath' + i
                        })
                        .style("pointer-events", "none")
                        .text(function (d) {
                            return '[' + d.type + ']'
                        });

                    svg.append('defs').append('marker')
                        .attr({
                            'id': 'arrowhead',
                            'viewBox': '-0 -5 10 10',
                            'refX': 25,
                            'refY': 0,
                            'orient': 'auto',
                            'markerWidth': 10,
                            'markerHeight': 10,
                            'xoverflow': 'visible'
                        })
                        .append('svg:path')
                        .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
                        .attr('fill', '#005A32')
                        .attr('stroke', '#005A32');

                    // force feed algo ticks for coordinate computation
                    force.on("tick", function () {
                        link.attr("x1", function (d) {
                            return d.source.x;
                        }).attr("y1", function (d) {
                            return d.source.y;
                        }).attr("x2", function (d) {
                            return d.target.x;
                        }).attr("y2", function (d) {
                            return d.target.y;
                        });

                        node.attr("cx", function (d) {
                            return d.x;
                        }).attr("cy", function (d) {
                            return d.y;
                        });

                        nodelabels.attr("x", function(d) { return d.x; })
                            .attr("y", function(d) { return d.y; });

                        edgepaths.attr('d', function (d) {
                            return 'M ' + d.source.x + ' ' + d.source.y + ' L ' +
                                d.target.x + ' ' + d.target.y;
                        });

                        edgelabels.attr('transform', function (d) {
                            if (d.target.x < d.source.x) {
                                const bbox = this.getBBox();
                                const rx = bbox.x + bbox.width / 2;
                                const ry = bbox.y + bbox.height / 2;
                                return 'rotate(180 ' + rx + ' ' + ry + ')';
                            }
                            else {
                                return 'rotate(0)';
                            }
                        });
                    });
                });

                this.causes = causes;
            }
        },
        watch: {
            '$route.params.id'() {
                this.graph = true;
                this.loadData();
            }
        }
    }
</script>