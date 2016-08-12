var express = require('express');
var app = express();

app.use(function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,X-PINGOTHER,Authorization,Content-Length");

    next();
});

var bodyParser = require('body-parser')
app.use(bodyParser.json()); // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({ // to support URL-encoded bodies
    extended: true
}));

app.get('/', function (req, res) {
    res.send('Server API CEP');
});

app.listen(3030, function () {
    console.log('Server on port 3030!');
});

var http = require('http');
var https = require('https');

var bdmCepPort = 8091; 

var redirect = function (host, port, path, method, req, res) {
        var options = {

            host: host,

            port: port,

            path: path,

            method: method,

            headers: req.headers
        };
        var post_data = JSON.stringify(req.body);
        if (method == 'POST') {
            options.headers['Content-Type'] = 'application/json;charset=UTF-8';
            options.headers['Content-Length'] = Buffer.byteLength(post_data);
        }


        var creq = http.request(options, function (cres) {


            cres.setEncoding('utf8');


            cres.on('data', function (chunk) {
                res.write(chunk);
            });

            cres.on('close', function () {

                res.writeHead(cres.statusCode);
                res.end();
            });

            cres.on('end', function () {
                //  res.writeHead(cres.statusCode);
                res.end();
            });

        }).on('error', function (e) {

            console.log(e.message);
            res.writeHead(500);
            res.end();
        });
        // post the data
        if (method == 'POST') {
            creq.write(post_data);
        }
        creq.end();
    }
    //Criar a Regra

app.get('/api/generator-rule', function (req, res) {
    redirect('srvbigpvlbr07.bs.br.bsch', bdmCepPort, '/ceprule', 'GET', req, res);
});

app.post('/api/generator-rule', function (req, res) {
    redirect('srvbigpvlbr07.bs.br.bsch', bdmCepPort, '/ceprule', 'POST', req, res);
});

app.delete('/api/generator-rule', function (req, res) {
    var id =  req.query.id;
    console.log('DELETE1 ' + id);
    redirect('srvbigpvlbr07.bs.br.bsch', bdmCepPort, '/ceprule/'+id, 'DELETE', req, res);
   
});


app.delete('/api/generator-rule/:id', function (req, res) {
   var id =  req.params.id;
   console.log('DELETE2 ' + id);
   redirect('srvbigpvlbr07.bs.br.bsch', bdmCepPort, '/ceprule/'+ id, 'DELETE', req, res);

});


//Edit
app.get('/api/generator-rule/:id', function (req, res) {
    console.log('GET-EDIT ' + +req.params.id);    
    redirect('srvbigpvlbr07.bs.br.bsch', bdmCepPort, '/ceprule/'+req.params.id, 'GET', req, res);
});

//Oauth
app.get('/api/oauth/token', function (req, res) {
    redirect('https://srvbigpvlbr07.bs.br.bsch', 8243, '/token', 'GET', req, res);
});
app.post('/api/oauth/token', function (req, res) {
    redirect('https://srvbigpvlbr07.bs.br.bsch', 8243, '/token', 'POST', req, res);
});

//endPoints
//tools
app.get('/api/tools', function (req, res) {
    console.log('GET-TOOL');
    redirect('srvbigpvlbr07.bs.br.bsch', bdmCepPort, '/tool/all', 'GET', req, res);
});
/*
//recebo o parametro Tool
app.param('tool', function (req, res, next, tool) {
    var modified = tool;
    // save name to the request
    req.tool = modified;
    next();
});
*/
//fields - nesse fields, tenho todos os campos , tais como fields e metrics
app.get('/api/tools/:tool', function (req, res, next) {
    console.log('GET-FIELDS '+req.params.tool);
    redirect('srvbigpvlbr07.bs.br.bsch', bdmCepPort, '/tool?id='+req.params.tool, 'GET', req, res);
});

