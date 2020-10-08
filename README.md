# Building

In order to build, you'll need to have the following installed:

  * A recent version of the Java Development Kit
  * The [`sbt` build tool][0]

Then, the site can be built with

```bash
$ sbt previewSite  # Build site and open it in a browser - use `Enter` to stop the preview
$ sbt packageSite  # Build a .zip of the whole site
```

# Notes about customization & styling

The site is built using a tool called [Paradox][1] and we are using a [Material UI theme overlay][2].
We use Paradox (over something more standard such as readthedocs) because it is very well integrated
with the language/tools/frameworks that we already develop in. As far as customization goes, Paradox
uses the StringTemplate language, and it possible to customize the look and feel of parts of the
page by overriding templates that are already defined. The Material UI theme overrides some of these
templates too, and introduces others.

Some useful links:

  * [Material UI theme notes on customization][6]
  * [Templates from the Material UI theme][5]
  * [Paradox’s own documentation about customization][3]
  * [Default page templates][4]

As a more concrete starting point, the black footer with navigation buttons comes from
<https://github.com/jonas/paradox-material-theme/blob/master/theme/src/main/assets/partials/footer.st>.
That footer can be replaced with just the red text `Customized footer` just by overriding that
template (the links above tell you where to put the file):

```bash
$ mkdir -p src/main/paradox/_template/partials
$ cat > src/main/paradox/_template/partials/footer.st
<footer class="md-footer" style="color:red">
Customized footer
</footer>
$ sbt previewSite      # Now the footer at the bottom of the page is different
```

[0]: https://www.scala-sbt.org/download.html
[1]: https://github.com/lightbend/paradox
[2]: https://github.com/jonas/paradox-material-theme
[3]: https://developer.lightbend.com/docs/paradox/current/customization/index.html
[4]: https://github.com/lightbend/paradox/tree/master/themes/generic/src/main/assets
[5]: https://github.com/jonas/paradox-material-theme/tree/master/theme/src/main/assets
[6]: https://github.com/jonas/paradox-material-theme/blob/master/src/main/paradox/customization.md
