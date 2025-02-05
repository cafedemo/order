#!/bin/bash

URL="https://apitest.sbcp.perfsbcp3.sbcp.io/order/"
CONTENT_TYPE="application/json"
ACCEPT="application/json"
TIMESTAMP=$(date +%s)
CSV_FILE="request_times$TIMESTAMP.csv"

echo "RequestNumber,TimeTaken(ms)" > $CSV_FILE

for i in {1..500}
do
  CUSTOMER_ID="Order_$((RANDOM % 10000 + 1))"
  ORDER_ID="order_$((RANDOM % 10000 + 1))"
  ORDER_STATUS="Initial_$((RANDOM % 10000+ 1))"
  BARISTA_ID="barista_$((RANDOM % 10000+ 1))"
  ITEM1_ID="item_$((RANDOM % 10000+ 1))"
  ITEM2_ID="item_$((RANDOM % 10000+ 1))"
  ITEM1_NAME="Item_$((RANDOM % 10000+ 1))"
  ITEM2_NAME="Item_$((RANDOM % 10000+ 1))"
  ITEM1_PRICE=$((RANDOM % 10000+ 1))
  ITEM2_PRICE=$((RANDOM % 10000+ 1))
  ITEM1_QUANTITY=$((RANDOM % 10000 + 1))
  ITEM2_QUANTITY=$((RANDOM % 10000 + 1))

  REQUEST_BODY=$(cat <<EOF
{
    "customerId": "$CUSTOMER_ID",
    "orderId": "$ORDER_ID",
    "orderStatus": "$ORDER_STATUS",
    "baristaId": "$BARISTA_ID",
    "orderItems": [
        {
            "itemId": "$ITEM1_ID",
            "quantity": $ITEM1_QUANTITY,
            "price": $ITEM1_PRICE,
            "itemName": "$ITEM1_NAME"
        },
        {
            "itemId": "$ITEM2_ID",
            "quantity": $ITEM2_QUANTITY,
            "price": $ITEM2_PRICE,
            "itemName": "$ITEM2_NAME"
        }
    ]
}
EOF
)

  echo "Sending request $i to $URL with body: $REQUEST_BODY"

  START_TIME=$(date +%s%3N)
  curl -X POST "$URL" \
       -H "Content-Type: $CONTENT_TYPE" \
       -H "Accept: $ACCEPT" \
       -d "$REQUEST_BODY"
  END_TIME=$(date +%s%3N)
  TIME_TAKEN=$((END_TIME - START_TIME))

  echo "Request $i sent took $TIME_TAKEN ms"
  echo "$i,$TIME_TAKEN" >> $CSV_FILE
done
