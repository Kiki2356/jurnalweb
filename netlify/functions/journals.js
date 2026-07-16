exports.handler = async function () {
  var token = process.env.GH_TOKEN;
  var repo = process.env.GH_REPO;

  if (!token || !repo) {
    return {
      statusCode: 500,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ error: 'GH_TOKEN dan GH_REPO belum diset di Environment Variables Netlify.' })
    };
  }

  var headers = { 'Authorization': 'token ' + token };

  try {
    var dirRes = await fetch('https://api.github.com/repos/' + repo + '/contents/journals/', { headers: headers });
    if (!dirRes.ok) {
      var err = await dirRes.json();
      throw new Error(err.message || 'HTTP ' + dirRes.status);
    }
    var files = await dirRes.json();
    if (!Array.isArray(files)) {
      return { statusCode: 200, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify([]) };
    }

    var mdFiles = files.filter(function (f) { return f.name.endsWith('.md'); });
    var promises = mdFiles.map(async function (f) {
      var fileRes = await fetch(f.url, { headers: headers });
      var data = await fileRes.json();
      return {
        name: f.name,
        content: Buffer.from(data.content, 'base64').toString('utf-8')
      };
    });

    var journals = await Promise.all(promises);

    return {
      statusCode: 200,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(journals)
    };
  } catch (err) {
    return {
      statusCode: 502,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ error: err.message })
    };
  }
};
