var Backbone = require('backbone');

module.exports = Backbone.Model.extend({
  urlRoot: '/logout',
  id:null,
  initialize: function () {
    console.log('logged out');
  },
  // updateURL: function(loggedOut){
  //   if(loggedOut){
  //     this.urlRoot = this.urlRoot + '/';
  //   } else {
  //     this.urlRoot = '/';
  //   }
  // }
});
