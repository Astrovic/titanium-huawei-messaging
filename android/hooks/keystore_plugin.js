exports.id = 'keystore_plugin';
exports.cliVersion = '>=3.2';
exports.init = init;

const path = require('path');
const fs = require('fs');

/**
 * Main entry point for our plugin which looks for the platform specific
 * plugin to invoke.
 *
 * @param {Object} logger The logger instance.
 * @param {Object} config The hook config.
 * @param {Object} cli The Titanium CLI instance.
 * @param {Object} appc The Appcelerator CLI instance.
 */
// eslint-disable-next-line no-unused-vars
function init(logger, config, cli, appc) {
	cli.on('build.pre.build', {
		post: function () {
			// Copy the keystore file
			// TODO: Make generic?
			const keystoreFileSrc = path.resolve('./AstrovicAppHw.keystore');
			const keystoreFileDest = path.resolve('./build/android/app/AstrovicAppHw.keystore');
			if (!fs.existsSync(keystoreFileSrc)) { return; }
			fs.copyFileSync(keystoreFileSrc, keystoreFileDest);			

			// 2. Extend project-wide build.gradle
			const projectBuildGradle = path.resolve('./build/android/build.gradle');
			if (!fs.existsSync(projectBuildGradle)) { return; }

			const projectBuildGradleContents = fs.readFileSync(projectBuildGradle, 'utf-8').toString();
			const updatedProjectBuildGradleContents = projectBuildGradleContents
				.split('repositories {').join('repositories {\n\t\tmaven { url \'https://developer.huawei.com/repo/\' }')
				.split('dependencies {').join('dependencies {\n\t\tclasspath \'com.huawei.agconnect:agcp:1.9.1.302\'');

			fs.writeFileSync(projectBuildGradle, updatedProjectBuildGradleContents);

			// 2. Extend app-wide build.gradle
			const appBuildGradle = path.resolve('./build/android/app/build.gradle');
			if (!fs.existsSync(appBuildGradle)) { return; }

			const appBuildGradleContents = fs.readFileSync(appBuildGradle, 'utf-8').toString();
			const updatedAppBuildGradleContents = appBuildGradleContents
				.split('dependencies {').join(`
					dependencies {
						implementation 'com.huawei.agconnect:agconnect-core:1.9.1.302'
						implementation 'com.huawei.agconnect:agconnect-appmessaging:1.9.1.302'
				`)
				.split('apply plugin: \'com.android.application\'').join('apply plugin: \'com.android.application\'\napply plugin: \'com.huawei.agconnect\'');

			fs.writeFileSync(appBuildGradle, updatedAppBuildGradleContents);
		}
	});
}
