var Backbone = require('backbone');

module.exports = Backbone.Model.extend({
  urlRoot: '/game',
  initialize: function(){
    console.log("from game model")
  }
});
