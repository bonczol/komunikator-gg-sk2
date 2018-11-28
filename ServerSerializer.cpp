#include "ServerSerializer.h"

void ServerSerializer::serialize() {
	this->ALL_CONVS = Conversation::ALL_CONVS;
	this->CLIENTS = Klient::CLIENTS;
	std::ofstream ofs("serializedServer.txt");
	boost::archive::text_oarchive oa(ofs);
	oa << *this;
	return;
}

void ServerSerializer::deserialize()
{
	std::ifstream ifs("serializeServer.txt");
	boost::archive::text_iarchive ia(ifs);
	ia >> *this;
	Conversation::ALL_CONVS = this->ALL_CONVS;
	Klient::CLIENTS = this->CLIENTS;
	return;
}


ServerSerializer::ServerSerializer(){}


ServerSerializer::~ServerSerializer(){}
