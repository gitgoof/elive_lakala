
Application.loadMode([
    'Panel','Label'
],'component');

Application.pages.test = function(){

    return Ext.create('LKLPanel', {
        pageName:'test',
        ui:AppDefaultUI,
        title:'TEST WEB',
        fullscreen:true,
        items:{
            xtype : 'lkllabel',
            style : {
                fontSize : '30px'
            },
            html  : 'WEB TEST PAGE'
        }
    });
};

Application.navigation.push(Application.pages.test());