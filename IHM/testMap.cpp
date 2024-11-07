#include <iostream>
#include <tinyxml2.h>
#include <unordered_map>
#include <vector>
#include <fstream>
#include <string>

using namespace tinyxml2;
using namespace std;

struct Node {
    double lat;
    double lon;
};

struct RoadSegment {
    string origine;
    string destination;
    double longueur;
};

int main() {
    XMLDocument doc;
    if (doc.LoadFile("petitPlan.xml") != XML_SUCCESS) {
        cerr << "Error loading XML file!" << endl;
        return 1;
    }

    XMLElement* root = doc.RootElement();

    unordered_map<string, Node> nodes;
    vector<RoadSegment> roadSegments;

    for (XMLElement* noeud = root->FirstChildElement("noeud"); noeud != nullptr; noeud = noeud->NextSiblingElement("noeud")) {
        string id = noeud->Attribute("id");
        double lat = noeud->DoubleAttribute("latitude");
        double lon = noeud->DoubleAttribute("longitude");

        nodes[id] = {lat, lon};
    }

    for (XMLElement* troncon = root->FirstChildElement("troncon"); troncon != nullptr; troncon = troncon->NextSiblingElement("troncon")) {
        string origine = troncon->Attribute("origine");
        string destination = troncon->Attribute("destination");
        double longueur = troncon->DoubleAttribute("longueur");

        roadSegments.push_back({origine, destination, longueur});
    }

    ofstream nodeFile("node.json");
    ofstream segmentFile("segment.json");

    if (!nodeFile || !segmentFile) {
        cerr << "Error opening files for writing!" << endl;
        return 1;
    }

    nodeFile << "{\n  \"nodes\": {\n";
    for (auto it = nodes.begin(); it != nodes.end(); ++it) {
        nodeFile << "    \"" << it->first << "\": { \"lat\": " << it->second.lat << ", \"lon\": " << it->second.lon << " }";
        if (next(it) != nodes.end()) {
            nodeFile << ",";
        }
        nodeFile << "\n";
    }
    nodeFile << "  }\n}";
    nodeFile.close();

    segmentFile << "{\n  \"roadSegments\": [\n";
    for (size_t i = 0; i < roadSegments.size(); ++i) {
        segmentFile << "    { \"origine\": \"" << roadSegments[i].origine << "\", \"destination\": \""
                    << roadSegments[i].destination << "\", \"longueur\": " << roadSegments[i].longueur << " }";
        if (i < roadSegments.size() - 1) {
            segmentFile << ",";
        }
        segmentFile << "\n";
    }
    segmentFile << "  ]\n}";
    segmentFile.close();

    cout << "Data has been written to node.txt and segment.txt successfully." << endl;

    return 0;
}
