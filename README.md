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
2. In the second, stay in the project root and run `yarn` then `yarn start`

Note that electron-build recomments using Yarn, so we do. However npm should also work.

## Design

The project has a fairly simple structure:

 * `scala_project_root` contains a normal scalajs project. This is configured to produce a CommonJS module that we can require from js in electron. This also means we can require node modules from scalajs. The module .js files are output to `scalajs_src` for ease of packaging.
 * `src` contains javascript and html resources.
   * `src/main.js` is the main entry point for the project, and runs the main process.
   * `src/index.html` is the single page for the application, and requires then runs the scalajs application compiled to `scalajs_src`.

The project doesn't use electron-webpack or electron-forge. As far as I can tell these only really help with transpiling javascript alternatives to javascript, and we are only using plain javascript and scalajs. The project is significantly quicker to start up electron than previous versions using electron-forge and electron-webpack. This does mean that hot-reload is not supported, which might not be a bad thing. Reload the page for scalajs changes, and restart with `npm start` for main.js changes.

For packaging, the project uses electron-builder, with a simple configuration in `package.json`. Run `yarn dist` to build and package. This will produce an nsis installer on Windows, and an AppImage and snap package on Linux. Should produce a dmg on OS X - not yet tried.

## Publishing and auto-update

This uses `electron-updater`. Configured in `package.json`, using the `publish` key in `build`.

This is currently configured to use `generic`, so it can run from a static server on localhost, however to configure for github use:

```json
    "publish": [
      {
        "provider": "github",
        "owner": "your-github-user",
        "repo": "your-repo"
      }
    ]
```    

`main.js` has a minimal updater implementation that will start the auto-updater when application starts (skipped in dev mode), and install updates when application is restarted.
So far this has only been tried on Windows.

To try this:

1. Package the application using `yarn dist`
2. Install the application using the installer in `dist` directory (the auto-updater is not started in dev mode). This is only needed for the first version used.
3. Increment the version number in `package.json`, and make some change to the application (e.g. the text in the card in `Main.scala`).
4. Package the new version using `yarn dist`
5. In the project root, run an http server serving the `dist` directory - this is our "generic" build server:
```
node_modules/.bin/http-server dist/ -p 8080
```
6. Run the installed application. It will download the update in the background (you can see this from the output of the http server)
7. When the update has downloaded, quit and rerun the application. The NSIS installer will run again, and you should be up to date.

See [electron updater example](https://github.com/iffy/electron-updater-example) for more details.

## TODO

1. It should be possible to replicate `main.js` in scalajs with an appropriate facade to electron API, and then simply require and run this from a stub in `main.js`.
2. Check running, packaging, installing (via dmg) and auto-updating on OS X
3. Add notifications in the main window when update is available, to demonstrate IPC.
