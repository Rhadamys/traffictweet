const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin')
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const webpack = require("webpack");
const CopyWebpackPlugin = require('copy-webpack-plugin');
module.exports = (options = {}) => ({
    entry: './main.js',
    output: {
        filename: 'scripts.js',
        path: path.resolve(__dirname, 'dist')
    },
    module: {
        rules: [
            {
                test: /\.vue$/,
                use:[{loader:"vue-loader"}]
            },
            {
                test: /\.scss$/,
                use: [{
                    loader: "style-loader" // creates style nodes from JS strings
                }, {
                    loader: "css-loader" // translates CSS into CommonJS
                }, {
                    loader: "sass-loader" // compiles Sass to CSS
                }]
            },
            {
                test:  /\.(jpeg|gif|png|svg|woff|ttf|wav|mp3)$/,
                loader: "file-loader?name=img/img-[hash:6].[ext]"
            }
        ]
    },
    plugins:options.dev?[
        new HtmlWebpackPlugin({
            template: 'index.html'
        })
    ]:[
        new HtmlWebpackPlugin({
            template: 'index.html'
        }),
        new CopyWebpackPlugin([{
            from: 'img', to: 'img'
        }]),
        new UglifyJSPlugin()
    ],
    devtool: options.dev ? '#eval-source-map' : '#source-map'
})
