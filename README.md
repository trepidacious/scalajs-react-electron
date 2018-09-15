# scalajs-react-electron
Minimal electron project with scalajs, react and electron-builder

## Building and running

The Scalajs code depends on several projects that are not yet published - so you will need to clone the following projects and run `sbt publishLocal` in each:

 * https://github.com/trepidacious/scalajs-react-material-ui
 * https://github.com/trepidacious/scalajs-react-material-icons
 * https://github.com/trepidacious/scalajs-react-downshift
 * https://github.com/trepidacious/scalajs-react-material-ui-extra

Next, clone this project, and start up two terminals:

1. In the first, change to the `scala_project_root` directory, then run `sbt fastOptJS`.
2. In the second, stay in the project root and run `npm install` then `npm start`

## Design

The project has a fairly simple structure:

 * `scala_project_root` contains a normal scalajs project. This is configured to produce a CommonJS module that we can require from js in electron. This also means we can require node modules from scalajs. The module .js files are output to `scalajs_src` for ease of packaging.
 * `src` contains javascript and html resources.
   * `src/main.js` is the main entry point for the project, and runs the main process.
   * `src/index.html` is the single page for the application, and requires then runs the scalajs application compiled to `scalajs_src`.

The project doesn't use electron-webpack or electron-forge. As far as I can tell these only really help with transpiling javascript alternatives to javascript, and we are only using plain javascript and scalajs. The project is significantly quicker to start up electron than previous versions using electron-forge and electron-webpack. This does mean that hot-reload is not supported, which might not be a bad thing. Reload the page for scalajs changes, and restart with `npm start` for main.js changes.

For packaging, the project uses electron-builder, with a simple configuration in `package.json`. Run `npm run dist` to build and package. This will produce an nsis installer on Windows, and an AppImage and snap package on Linux. Should produce a dmg on OS X - not yet tried.

## TODO

1. It should be possible to replicate `main.js` in scalajs with an appropriate facade to electron API, and then simply require and run this from a stub in `main.js`.
2. Use Yarn rather than NPM, as recommented by electron-builder.
3. Add more information on packaging
4. Test and demonstrate auto-update.
5. Check build on OS X (dmg)
