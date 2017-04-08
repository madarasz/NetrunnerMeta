module.exports = function(grunt) {

    // Project configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        // concatenate JS and CSS
        concat: {
            js: {
                src: [
                    '../src/main/resources/static/js/jquery-1.11.3.min.js',
                    '../src/main/resources/static/js/bootstrap.min.js',
                    '../src/main/resources/static/js/main.js',
                    '!../src/main/resources/static/js/ktm.concat.js',
                    '!../src/main/resources/static/js/ktm.concat.min.js'
                ],
                dest: '../src/main/resources/static/js/ktm.concat.js'
            },
            css: {
                src: [
                    '../src/main/resources/static/css/*.css',
                    '!../src/main/resources/static/css/ktm.concat.css',
                    '!../src/main/resources/static/css/ktm.concat.min.css'
                ],
                dest: '../src/main/resources/static/css/ktm.concat.css'
            }
        },

        // uglify JS
        uglify: {
            my_target: {
                files: {
                    '../src/main/resources/static/js/ktm.concat.min.js':
                        '../src/main/resources/static/js/ktm.concat.js'
                }
            }
        },

        // minify CSS
        cssmin:  {
            css:{
                src: '../src/main/resources/static/css/ktm.concat.css',
                dest: '../src/main/resources/static/css/ktm.concat.min.css'
            }
        },

        // watch if anything changes
        watch: {
            files: [
                '../src/main/resources/static/js/*.js',
                '../src/main/resources/static/css/*.css',
                '!../src/main/resources/static/css/ktm.concat.css',
                '!../src/main/resources/static/css/ktm.concat.min.css',
                '!../src/main/resources/static/js/ktm.concat.min.js',
                '!../src/main/resources/static/js/ktm.concat.js'
            ],
            tasks: ['concat', 'cssmin', 'uglify']
        },

        //backupDB
        compress: {
            main: {
                options: {
                    archive: '../../KnowTheMetaDB/' + grunt.template.today("yyyy-mm-dd") + '.zip',
                    mode: 'zip',
                    pretty: true
                },
                files: [
                    { src: ['../netrunner.db/**',
                        '!../netrunner.db/messages.log', '!../netrunner.db/nioneo_logical.log.v*'] }
                ]
            }
        }
    });
    // Load the plugins
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-css');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-compress');

    // Default task
    grunt.registerTask('default', ['concat', 'cssmin', 'uglify']);
    // backup DB
    grunt.registerTask('backupdb', 'compress');
};