#!/usr/bin/env python3

import datetime
import json

from flask import Flask, jsonify, request
from notion.client import NotionClient
from notion.collection import NotionDate

app = Flask(__name__)


@app.route('/quotes/<api_key>/<encoded_collection_url>', methods=['GET'])
def get_database_entries(api_key: str, encoded_collection_url: str):
    # Decode the URL
    collection_url = bytes.fromhex(encoded_collection_url).decode('utf-8')

    # Create a Notion client
    client = NotionClient(token_v2=api_key)

    # Get the rows from the collection
    collection_view = client.get_collection_view(collection_url)
    result = collection_view.default_query().execute()

    # Process the rows
    quotes = []
    for row_id, row in enumerate(result):
        quotes.append({
            'id': row_id,
            'sourceTitle': row.source_title,
            'sourceURL': row.source_url,
            'content': row.quote,
            'creationDate': row.creation_date.start.strftime('%d.%m.%Y'),
            'labels': row.labels
        }) # yapf: disable

    return jsonify(quotes)


@app.route('/quotes/<api_key>/<encoded_collection_url>', methods=['POST'])
def update_database_entries(api_key: str, encoded_collection_url: str):
    # Decode the URL
    collection_url = bytes.fromhex(encoded_collection_url).decode('utf-8')

    # Create a Notion client
    client = NotionClient(token_v2=api_key)

    # Get the rows from the collection
    collection_view = client.get_collection_view(collection_url)
    result = collection_view.build_query()

    # Remove all rows in the collection
    collection_view = client.get_collection_view(collection_url)
    result = collection_view.default_query().execute()
    for row in result:
        row.remove()

    # Insert rows for each quote in the request
    print(request.data)
    quotes_list = json.loads(request.data)
    for quote in quotes_list:
        row = collection_view.collection.add_row()
        row.quote = quote["content"]
        row.source_title = quote["sourceTitle"]
        row.source_url = quote["sourceURL"]
        row.creation_date = NotionDate(start=datetime.datetime.strptime(
            quote["creationDate"], "%d.%m.%Y"))
        row.labels = quote["labels"]

    return ""


if __name__ == '__main__':
    app.run(debug=True)
