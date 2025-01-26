const { ModuleFederationPlugin } = require('@module-federation/enhanced/webpack');

module.exports = () => {
  return {
    optimization: {
      moduleIds: 'named',
      chunkIds: 'named',
      runtimeChunk: false,
    },

    plugins: [new ModuleFederationPlugin(require('../module-federation.config.cjs'))],
    output: {
      publicPath: 'auto',
      scriptType: 'text/javascript',
    },
  };
};
