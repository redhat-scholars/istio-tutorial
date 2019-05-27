"use strict";

const connect = require("gulp-connect");
const fs = require("fs");
const generator = require("@antora/site-generator-default");
const gulp = require("gulp");
const yaml = require("yaml-js");

let filename = "dev-site.yml";
let args = ["--playbook", filename];

gulp.task("build", function(cb) {
  /**
   * Use the '@antora/site-generator-default' node module to build.
   * It's analogous to `$ antora --playbook local-antora-playbook.yml`.
   * Having access to the generator in code may be useful for other
   * reasons in the future (i.e to implement custom features).
   * NOTE: As opposed to building with the CLI, this method doesn't use
   * a separate process for each run. So if a build error occurs with the `gulp`
   * command it can be useful to check if it also happens with the CLI command.
   */
  generator(args, process.env)
    .then(() => {
      cb();
    })
    .catch(err => {
      console.log(err);
      cb();
    });
});

gulp.task("preview", ["build"], function() {
  /**
   * Remove the line gulp.src('README.adoc')
   * This is placeholder code to follow the gulp-connect
   * example. Could not make it work any other way.
   */
  gulp.src("README.adoc").pipe(connect.reload());
});

gulp.task("watch", function() {
  let json_content = fs.readFileSync(`${__dirname}/${filename}`, "UTF-8");
  let yaml_content = yaml.load(json_content);
  let dirs = yaml_content.content.sources.map(source => [
    `${source.url}/**/**.yml`,
    `${source.url}/**/**.adoc`,
    `${source.url}/**/**.hbs`
  ]);
  dirs.push(["dev-site.yml"]);
  gulp.watch(dirs, ["preview"]);
});

gulp.task("connect", function() {
  connect.server({
    port: 5353,
    name: "Dev Server",
    livereload: true,
    root: "gh-pages"
  });
});

gulp.task("default", ["connect", "watch", "build"]);
