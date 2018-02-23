#!/bin/sh

TEST=$1

TOPIC_NAME="products"
SUBSCRIPTION_NAME="dataflow-pipeline-products"

gcloud pubsub topics create ${TOPIC_NAME}
gcloud pubsub subscriptions create --topic products ${SUBSCRIPTION_NAME}

if test -n "$TEST"; then
  gcloud pubsub topics publish ${TOPIC_NAME} --message '{"sku":1,"name":"ACME - Super Vaccum Cleaner","type":"HardGood","price":2699.99,"upc":"883049375595","category":[{"id":"abcat0900000","name":"Appliances"},{"id":"abcat0901000","name":"Refrigerators"},{"id":"pcmcat367400050001","name":"All Refrigerators"}],"shipping":"","description":"Electric controls; StoreRight&#8482; dual-cooling system; Exterior Ice and Water Dispensers; Spill-Proof Shelves; Pizza Pocket; Two-Tier Freezer Storage","manufacturer":"Whirlpool","model":"WRF767SDEM","url":"http://www.bestbuy.com/site/whirlpool-27-0-cu-ft-french-door-refrigerator-with-thru-the-door-ice-and-water-monochromatic-stainless-steel/5222500.p?id=bb5222500&skuId=5222500&cmp=RMXCC","image":"http://img.bbystatic.com/BestBuy_US/images/products/5222/5222500_sa.jpg"}'
  gcloud pubsub subscriptions pull --auto-ack ${SUBSCRIPTION_NAME}
fi