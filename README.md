# Building

In order to build, you'll need to have the following installed:

  * A recent version of the Java Development Kit
  * The [`sbt` build tool][0]

Then, the site can be built with

```bash
sbt previewSite  # Build site and open it in a browser - use `Enter` to stop the preview
sbt packageSite  # Build a .zip of the whole site
```

# Notes about customization & styling

The site is built using a tool called [Paradox][1] and we are using a [Material UI theme overlay][2].
We use Paradox (over something more standard such as readthedocs) because it is very well integrated
with the language/tools/frameworks that we already develop in. As far as customization goes, Paradox
uses the StringTemplate language, and it possible to customize the look and feel of parts of the
page by overriding templates that are already defined. The Material UI theme overrides some of these
templates too, and introduces others.

Some useful links:

  * [Paradox’s own documentation about customization][3]
  * [Default page templates][4]
  * [Templates from the Material UI theme][5]

As a more concrete starting point, the black footer with navigation buttons comes from
<https://github.com/jonas/paradox-material-theme/blob/master/theme/src/main/assets/partials/footer.st>.

[0]: https://www.scala-sbt.org/download.html
[1]: https://github.com/lightbend/paradox
[2]: https://github.com/jonas/paradox-material-theme
[3]: https://developer.lightbend.com/docs/paradox/current/customization/index.html
[4]: https://github.com/lightbend/paradox/tree/master/themes/generic/src/main/assets
[5]: https://github.com/jonas/paradox-material-theme/tree/master/theme/src/main/assets
