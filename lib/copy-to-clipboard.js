const BlockCopyToClipboardMacro = (() => {
  const $context = Symbol("context");
  const superclass = Opal.module(null, "Asciidoctor").Extensions
    .BlockMacroProcessor;
  const scope = Opal.klass(
    Opal.module(null, "Antora"),
    superclass,
    "BlockCopyToClipboardMacro",
    function() {}
  );

  Opal.defn(scope, "$initialize", function initialize(name, config, context) {
    Opal.send(
      this,
      Opal.find_super_dispatcher(this, "initialize", initialize),
      [name, config]
    );
    this[$context] = context;
  });

  Opal.defn(scope, "$process", function(parent, target, attrs) {
    const t = target.startsWith(":") ? target.substr(1) : target;
    //console.log("target:", t);
    const createHtmlFragment = html => this.createBlock(parent, "pass", html);
    const html = `<button class="copybtn" title="Copy to clipboard" data-clipboard-target="#${t}"><i class="fa fa-copy"></i></button><br/>`;
    parent.blocks.push(createHtmlFragment(html));
  });

  return scope;
})();

module.exports.register = (registry, context) => {
  registry.blockMacro(
    BlockCopyToClipboardMacro.$new("copyToClipboard", Opal.hash(), context)
  );
};
