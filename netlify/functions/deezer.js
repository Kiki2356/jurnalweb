exports.handler = async function (event) {
  var q = event.queryStringParameters.q;
  if (!q) {
    return { statusCode: 400, body: JSON.stringify({ error: 'missing q' }) };
  }

  try {
    var res = await fetch('https://api.deezer.com/search?q=' + encodeURIComponent(q));
    var data = await res.json();
    return {
      statusCode: 200,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    };
  } catch (err) {
    return {
      statusCode: 502,
      body: JSON.stringify({ error: err.message })
    };
  }
};
