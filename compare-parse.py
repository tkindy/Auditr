import sys
import json
import pprint

def load_json(filename):
  with open(filename) as json_file:
    return json.load(json_file)

def build_group_dict(parse):
  return {group["name"]: group for group in parse["requirementGroups"]}

parse1 = load_json(sys.argv[1])
parse2 = load_json(sys.argv[2])

groups1 = build_group_dict(parse1)
groups2 = build_group_dict(parse2)

res = {}

for name in groups1.keys():
  if name in res.keys():
    continue

  reqs1 = set([req["name"] for req in groups1[name]["requirements"]])
  reqs2 = set([req["name"] for req in groups2[name]["requirements"]]) if name in groups2.keys() else []
  diff1 = reqs1 - reqs2
  diff2 = reqs2 - reqs1
  
  if diff1 or diff2:
    res[name] = (diff1, diff2)

for name in groups2.keys():
  if name in res.keys():
    continue

  reqs1 = set([req["name"] for req in groups2[name]["requirements"]])
  reqs2 = set([req["name"] for req in groups1[name]["requirements"]]) if name in groups1.keys() else []
  diff1 = reqs1 - reqs2
  diff2 = reqs2 - reqs1
  
  if diff1 or diff2:
    res[name] = (diff2, diff1)

pp = pprint.PrettyPrinter(indent=2)
pp.pprint(res)
