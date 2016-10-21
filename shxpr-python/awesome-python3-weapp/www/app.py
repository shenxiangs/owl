#!/usr/bin/env python3
#-*-coding: utf-8 -*-

_author_= 'shx'

'''
async web applicatio

'''
import logging;logging.basicConfig(level=logging.INFO)
import asyncio,os,json,time
from datetime import datetime
from aiohttp import web

def index(requst):
    return web.Response(body=b'<h1>Awesome</h1>')


async def init(loop):
    app = web.Application(loop=loop)
    app.router.add_route('GET','/',index)
    srv = await loop.create_server(app.make_handler(),'192.168.10.128',9000)
    logging.info('server start at http://192.168.10.128:9000...')
    return srv

loop  = asyncio.get_event_loop()
loop.run_until_complete(init(loop))
loop.run_forever()
