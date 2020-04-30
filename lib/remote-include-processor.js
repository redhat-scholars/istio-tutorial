module.exports = function () {
    this.includeProcessor(function () {
      this.$option('position', '>>')
      this.handles((target) => target.startsWith('http'))
      this.process((doc, reader, target, attrs) => {
        const contents = require('child_process').execFileSync('curl', ['--silent', '-L', target], { encoding: 'utf8' })
        reader.pushInclude(contents, target, target, 1, attrs)
      })
    })
  }