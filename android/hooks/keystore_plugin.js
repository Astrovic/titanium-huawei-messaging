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
			const servicesFileSrc = path.resolve('./AstrovicAppHw.keystore');
			const servicesFileDest = path.resolve('./build/android/app/AstrovicAppHw.keystore');
			if (!fs.existsSync(servicesFileSrc)) { return; }
			fs.copyFileSync(servicesFileSrc, servicesFileDest);
		}
	});
}
